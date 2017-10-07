package org.platypus.modules.parser.visitor

import org.antlr.v4.runtime.tree.RuleNode
import org.platypus.modules.data.SimplePropertyType
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.lang.kotlin.KotlinParserBaseVisitor

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */


object SimplePropertyVisitor : KotlinParserBaseVisitor<Set<ModelField>>() {

    override fun visitPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext): Set<ModelField> {
        if (ctx.expression() != null) {
            println("Parsing field ${ctx.variableDeclarationEntry().SimpleName()}")
            if (ctx.modifiers().text != "public") {
                //error modifier should be public on none
            }
            if (ctx.multipleVariableDeclarations() != null) {
                // error only on field par line
            }
            val type = FieldType.valueOf(NewFieldPropertyType.visitExpression(ctx.expression()).toUpperCase())
            val typeName = NewFieldPropertyValidator.visitExpression(ctx.expression())
            val propertyName = ctx.variableDeclarationEntry().text
        } else {
            return emptySet()
        }
    }
    override fun defaultResult(): Set<ModelField> {
        return emptySet()
    }

    override fun aggregateResult(aggregate: Set<ModelField>, nextResult: Set<ModelField>): Set<ModelField> {
        return aggregate + nextResult
    }
}

object NewFieldPropertyValidator : KotlinParserBaseVisitor<SimplePropertyType>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): SimplePropertyType? {
        return if (NewFieldPropertyType.visitAtomicExpression(ctx.atomicExpression()) == "newField") {
            SimplePropertyType.valueOf(NewFieldPropertyType.visitPostfixUnaryOperation(ctx.postfixUnaryOperation()[0]))
        } else {
            super.visitPostfixUnaryExpression(ctx)
        }
    }

    override fun defaultResult() = SimplePropertyType.NONE
    override fun shouldVisitNextChild(node: RuleNode, currentResult: SimplePropertyType) = currentResult != SimplePropertyType.NONE
}

object NewFieldPropertyType : KotlinParserBaseVisitor<String>() {
    override fun visitAtomicExpression(ctx: KotlinParser.AtomicExpressionContext) = ctx.identifier().SimpleName().text
    override fun defaultResult() = ""
    override fun shouldVisitNextChild(node: RuleNode, currentResult: String) = currentResult.isBlank()


}