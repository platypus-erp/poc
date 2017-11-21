package org.platypus.modules.parser.visitor.methods

import org.antlr.v4.runtime.tree.RuleNode
import org.platypus.modules.data.MethodType
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.lang.kotlin.KotlinParserBaseVisitor
import org.platypus.modules.parser.visitor.GetAtomicExpressionName

object ApiMethodVisitor : KotlinParserBaseVisitor<NewMethodFieldAntlr>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): NewMethodFieldAntlr? {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        if (mName == "newMethod") {
            val type = GetNewMethodType.visitPostfixUnaryOperation(ctx.postfixUnaryOperation(0))!!

            val res = when (type) {
//                MethodType.ONE ->{
//                    val returnType = GetReturnType.visitPostfixUnaryOperation(ctx.postfixUnaryOperation(0))
//                    val paramType = GetParameterOneOrMulti.visitPostfixUnaryExpression(ctx)
//                    NewMethodFieldAntlr(type, paramType, returnType)
//                }
                MethodType.MULTI -> {
                    val returnType = GetReturnType.visitPostfixUnaryOperation(ctx.postfixUnaryOperation(0))
                    val paramType = GetParameterOneOrMulti.visitPostfixUnaryExpression(ctx)
                    NewMethodFieldAntlr(type, paramType, returnType)
                }
//                MethodType.STATIC -> {
//                    val returnType = GetReturnType.visitPostfixUnaryOperation(ctx.postfixUnaryOperation(0))
//                    val paramType = GetParameterStatic.visitPostfixUnaryExpression(ctx)
//                    NewMethodFieldAntlr(type, paramType, returnType)
//                }
                MethodType.GROUP -> {
                    NewMethodFieldAntlr(type, "", "")
                }
                MethodType.NONE -> {
                    NewMethodFieldAntlr(type, "", "")
                }
            }
            return res
        }
        return NewMethodFieldAntlr(MethodType.NONE, "", "")
    }

    override fun defaultResult() = null
    override fun shouldVisitNextChild(node: RuleNode, currentResult: NewMethodFieldAntlr?) = currentResult == null
}