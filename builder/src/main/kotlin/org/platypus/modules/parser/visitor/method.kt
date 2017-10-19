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
data class NewMethodFieldAntlr(val type: MethodType, val paramType: String, val returnType: String?)

object MethodVisitor : KotlinParserBaseVisitor<Set<ModelMethod>>() {

    override fun visitPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext): Set<ModelMethod> {
        if (ctx.expression() != null) {
            val propertyName = ctx.variableDeclarationEntry().text

            if (ctx.modifiers().text != "public") {
                //error modifier should be public on none
            }
            if (ctx.multipleVariableDeclarations() != null) {
                // error only on field par line
            }
            return if (MethodValidator.visitPropertyDeclaration(ctx)){
                val meth = ApiMethodVisitor.visitExpression(ctx.expression())
                return if (meth != null) {
                    println("\tParsing method $propertyName, ${meth.type}")
                    setOf(ModelMethod(propertyName, meth.type, meth.paramType, meth.returnType))
                } else{
                    emptySet()
                }
            } else {
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

            val res = when (type) {
                MethodType.ONE ->{
                    val returnType = if (ctx.callSuffix() != null){
                        GetReturnType.visitPostfixUnaryExpression(ctx)
                    } else{
                        ""
                    }
                    val paramType = GetParameterOneOrMulti.visitPostfixUnaryExpression(ctx)
                    NewMethodFieldAntlr(type, paramType, returnType)
                }
                MethodType.MULTI -> {
                    val returnType = if (ctx.callSuffix() != null){
                        GetReturnType.visitPostfixUnaryExpression(ctx)
                    } else{
                        ""
                    }
                    val paramType = GetParameterOneOrMulti.visitPostfixUnaryExpression(ctx)
                    NewMethodFieldAntlr(type, paramType, returnType)
                }
                MethodType.STATIC -> {
                    val returnType = if (ctx.callSuffix() != null){
                        GetReturnType.visitPostfixUnaryExpression(ctx)
                    } else{
                        ""
                    }
                    val paramType = GetParameterStatic.visitPostfixUnaryExpression(ctx)
                    NewMethodFieldAntlr(type, paramType, returnType)
                }
                MethodType.GROUP -> {
                    NewMethodFieldAntlr(type, "", "")
                }
                MethodType.NONE -> {
                    NewMethodFieldAntlr(type, "", "")
                }
            }
            return res
        }
        return null
    }

    override fun defaultResult() = null
    override fun shouldVisitNextChild(node: RuleNode, currentResult: NewMethodFieldAntlr?) = currentResult == null
}

object MethodValidator : KotlinParserBaseVisitor<Boolean>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): Boolean {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        return mName == "newfield" || mName == "compute" || mName == "computeStore"
    }

    override fun defaultResult() = false
    override fun shouldVisitNextChild(node: RuleNode, currentResult: Boolean) = !currentResult
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

