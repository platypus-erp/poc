package org.platypus.modules.parser.visitor

import org.antlr.v4.runtime.tree.RuleNode
import org.platypus.modules.data.FieldTypeCompute
import org.platypus.modules.data.MethodType
import org.platypus.modules.data.ModelMethod
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.lang.kotlin.KotlinParserBaseVisitor

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */
data class NewMethodFieldAntlr(val type: MethodType, val paramType: String, val returnType: String)

object MethodVisitor : KotlinParserBaseVisitor<Set<ModelMethod>>() {

    override fun visitPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext): Set<ModelMethod> {
        if (ctx.expression() != null) {
            val propertyName = ctx.variableDeclarationEntry().text
            println("Parsing field $propertyName")
            if (ctx.modifiers().text != "public") {
                //error modifier should be public on none
            }
            if (ctx.multipleVariableDeclarations() != null) {
                // error only on field par line
            }
            val meth = ApiMethodVisitor.visitExpression(ctx.expression())
            return if (meth != null) {
                setOf(ModelMethod(propertyName, meth.type, meth.paramType, meth.returnType))
            } else{
                emptySet()
            }
        } else {
            return emptySet()
        }
    }

    override fun defaultResult() = emptySet<ModelMethod>()
    override fun aggregateResult(aggregate: Set<ModelMethod>, nextResult: Set<ModelMethod>) = aggregate + nextResult
}

object ApiMethodVisitor : KotlinParserBaseVisitor<NewMethodFieldAntlr?>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): NewMethodFieldAntlr? {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        if (mName == "newMethod") {
            val type = GetNewMethodType.visitPostfixUnaryOperation(ctx.postfixUnaryOperation(0))!!
            val returnType = GetReturnType.visitValueArguments(ctx.callSuffix().valueArguments())!!
            val paramType = when (type) {
                MethodType.ONE -> GetParameterOneOrMulti.visitAnnotatedLambda(ctx.callSuffix().annotatedLambda())
                MethodType.MULTI -> GetParameterOneOrMulti.visitAnnotatedLambda(ctx.callSuffix().annotatedLambda())
                MethodType.STATIC -> GetParameterStatic.visitAnnotatedLambda(ctx.callSuffix().annotatedLambda())
                MethodType.NONE -> ""
            }
            return NewMethodFieldAntlr(type, returnType, paramType)
        }
        return null
    }

    override fun defaultResult() = null
    override fun shouldVisitNextChild(node: RuleNode, currentResult: NewMethodFieldAntlr?) = currentResult == null
}

object GetNewMethodType : KotlinParserBaseVisitor<MethodType>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): MethodType {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        println(mName)
        return MethodType.valueOf(mName.toUpperCase())
    }

    override fun defaultResult() = MethodType.NONE
    override fun shouldVisitNextChild(node: RuleNode, currentResult: MethodType) = currentResult == MethodType.NONE
}

object GetReturnType : KotlinParserBaseVisitor<String>() {

    override fun visitSimpleUserType(ctx: KotlinParser.SimpleUserTypeContext): String {
        return ctx.SimpleName().text ?: defaultResult()
    }

    override fun defaultResult() = ""
    override fun shouldVisitNextChild(node: RuleNode, currentResult: String) = currentResult == ""
}

object GetParameterOneOrMulti : KotlinParserBaseVisitor<String>() {

    override fun visitAnnotatedLambda(ctx: KotlinParser.AnnotatedLambdaContext): String {
        return ctx.functionLiteral().functionLiteralparameter(1).type().typeDescriptor().userType().simpleUserType(0).SimpleName().text
    }
}

object GetParameterStatic : KotlinParserBaseVisitor<String>() {

    override fun visitAnnotatedLambda(ctx: KotlinParser.AnnotatedLambdaContext): String {
        return ctx.functionLiteral().functionLiteralparameter(0)
                .type().typeDescriptor().userType().simpleUserType(0).SimpleName().text
    }
}

