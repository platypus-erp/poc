package org.platypus.modules.parser.visitor.methods

import org.antlr.v4.runtime.tree.RuleNode
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.lang.kotlin.KotlinParserBaseVisitor
import org.platypus.modules.parser.visitor.GetAtomicExpressionName

object MethodValidator : KotlinParserBaseVisitor<Boolean>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): Boolean {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        return mName == "newMethod" || mName == "compute" || mName == "computeStore"
    }

    override fun defaultResult() = false
    override fun shouldVisitNextChild(node: RuleNode, currentResult: Boolean) = !currentResult
}