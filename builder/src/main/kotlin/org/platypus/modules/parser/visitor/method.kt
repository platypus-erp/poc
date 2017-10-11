package org.platypus.modules.parser.visitor

import org.antlr.v4.runtime.tree.RuleNode
import org.platypus.modules.data.*
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.lang.kotlin.KotlinParserBaseVisitor

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */
data class NewMethodFieldAntlr(val name: String, val type: MethodType, val param: String)

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
            return emptySet()
        } else {
            return emptySet()
        }
    }

    override fun defaultResult() = emptySet<ModelMethod>()
    override fun aggregateResult(aggregate: Set<ModelMethod>, nextResult: Set<ModelMethod>) = aggregate + nextResult
}

object ApiMethodVisitor : KotlinParserBaseVisitor<NewMethodFieldAntlr>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): NewMethodFieldAntlr? {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
//        if (mName == "newfield") {
//            val type = GetNewMethodType.visitPostfixUnaryOperation(ctx.postfixUnaryOperation(0))
//            return NewSimpleFieldAntlr(false, FieldTypeCompute.NEWFIELD, type)
//        }
        return null
    }

    override fun defaultResult() = null
    override fun shouldVisitNextChild(node: RuleNode, currentResult: NewMethodFieldAntlr?) = currentResult == null
}

object GetNewMethodType : KotlinParserBaseVisitor<SimplePropertyType>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): SimplePropertyType {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        println(mName)
        return SimplePropertyType.valueOf(mName.toUpperCase())
    }

    override fun defaultResult() = SimplePropertyType.NONE
    override fun shouldVisitNextChild(node: RuleNode, currentResult: SimplePropertyType) = currentResult == SimplePropertyType.NONE
}

