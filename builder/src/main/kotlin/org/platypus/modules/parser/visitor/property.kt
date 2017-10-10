package org.platypus.modules.parser.visitor

import org.antlr.v4.runtime.tree.RuleNode
import org.platypus.modules.data.FieldTypeCompute
import org.platypus.modules.data.ModelSimpleProperty
import org.platypus.modules.data.SimplePropertyType
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.lang.kotlin.KotlinParserBaseVisitor

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */

data class NewMethodAntlr(val readonly: Boolean, val compute: FieldTypeCompute, val type: SimplePropertyType)

object SimplePropertyVisitor : KotlinParserBaseVisitor<Set<ModelSimpleProperty>>() {

    override fun visitPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext): Set<ModelSimpleProperty> {
        if (ctx.expression() != null) {
            val propertyName = ctx.variableDeclarationEntry().text
            println("Parsing field $propertyName")
            if (ctx.modifiers().text != "public") {
                //error modifier should be public on none
            }
            if (ctx.multipleVariableDeclarations() != null) {
                // error only on field par line
            }
            val mwthodname = GetMethodName.visitExpression(ctx.expression())
            return setOf(ModelSimpleProperty(propertyName, mwthodname.readonly, mwthodname.compute, mwthodname.type))
        } else {
            return emptySet()
        }
        return emptySet()
    }

    override fun defaultResult(): Set<ModelSimpleProperty> {
        return emptySet()
    }

    override fun aggregateResult(aggregate: Set<ModelSimpleProperty>, nextResult: Set<ModelSimpleProperty>): Set<ModelSimpleProperty> {
        return aggregate + nextResult
    }
}

object GetMethodName : KotlinParserBaseVisitor<NewMethodAntlr>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): NewMethodAntlr? {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        if (mName == "newfield") {
            val type = GetNewFieldType.visitPostfixUnaryOperation(ctx.postfixUnaryOperation(0))
            return NewMethodAntlr(false, FieldTypeCompute.NEWFIELD, type)
        }
//        if (mName == "compute") {
//             return super.visitPostfixUnaryExpression(ctx)
//        }
//        if (mName == "computeStore") {
//            return super.visitPostfixUnaryExpression(ctx)
//        }
        return super.visitPostfixUnaryExpression(ctx)
    }

    override fun defaultResult() = null
    override fun shouldVisitNextChild(node: RuleNode, currentResult: NewMethodAntlr?) = currentResult == null
}

object GetNewFieldType : KotlinParserBaseVisitor<SimplePropertyType>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): SimplePropertyType {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        println(mName)
        return SimplePropertyType.valueOf(mName.toUpperCase())
    }

    override fun defaultResult() = SimplePropertyType.NONE
    override fun shouldVisitNextChild(node: RuleNode, currentResult: SimplePropertyType) = currentResult == SimplePropertyType.NONE
}

object GetReadOnly : KotlinParserBaseVisitor<Boolean>() {

    override fun defaultResult() = false
    override fun shouldVisitNextChild(node: RuleNode, currentResult: Boolean) = false
}

object GetAtomicExpressionName : KotlinParserBaseVisitor<String>() {
    override fun visitAtomicExpression(ctx: KotlinParser.AtomicExpressionContext) = ctx.identifier().SimpleName().text
    override fun defaultResult() = ""
    override fun shouldVisitNextChild(node: RuleNode, currentResult: String) = currentResult.isBlank()


}