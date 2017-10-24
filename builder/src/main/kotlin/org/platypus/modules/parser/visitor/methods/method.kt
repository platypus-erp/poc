package org.platypus.modules.parser.visitor.methods

import org.antlr.v4.runtime.tree.RuleNode
import org.platypus.modules.data.MethodType
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.lang.kotlin.KotlinParserBaseVisitor
import org.platypus.modules.parser.visitor.GetAtomicExpressionName

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */
data class NewMethodFieldAntlr(val type: MethodType, val paramType: String, val returnType: String)

object GetNewMethodType : KotlinParserBaseVisitor<MethodType>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): MethodType {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        return MethodType.valueOf(mName.toUpperCase())
    }

    override fun defaultResult() = MethodType.NONE
    override fun shouldVisitNextChild(node: RuleNode, currentResult: MethodType) = currentResult == MethodType.NONE
}

object GetReturnType : KotlinParserBaseVisitor<String>() {

    override fun visitCallSuffix(ctx: KotlinParser.CallSuffixContext): String {
        return if (ctx.valueArguments() != null){
            this.visitValueArguments(ctx.valueArguments())
        } else{
            defaultResult()
        }
    }

    override fun visitSimpleUserType(ctx: KotlinParser.SimpleUserTypeContext): String {
        print(ctx.SimpleName().text)
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

