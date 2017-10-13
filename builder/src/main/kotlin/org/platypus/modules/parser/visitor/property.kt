package org.platypus.modules.parser.visitor

import org.antlr.v4.runtime.tree.RuleNode
import org.platypus.modules.data.FieldTypeCompute
import org.platypus.modules.data.ModelProperty
import org.platypus.modules.data.SimplePropertyType
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.lang.kotlin.KotlinParserBaseVisitor

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */

data class NewSimpleFieldAntlr(val readonly: Boolean, val compute: FieldTypeCompute, val type: SimplePropertyType)

data class NewReletationFieldAntlr(val m: NewSimpleFieldAntlr, val target: Pair<String, String>)

object PropertyVisitor : KotlinParserBaseVisitor<Set<ModelProperty>>() {

    override fun visitPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext): Set<ModelProperty> {
        if (ctx.expression() != null) {
            val propertyName = ctx.variableDeclarationEntry().text
            println("Parsing field $propertyName")
            if (ctx.modifiers().text != "public") {
                //error modifier should be public on none
            }
            if (ctx.multipleVariableDeclarations() != null) {
                // error only on field par line
            }
            val simpleProperty = SimplePropertyVisitor.visitExpression(ctx.expression())
            return if (simpleProperty != null) {
                setOf(ModelProperty(propertyName, simpleProperty.readonly, simpleProperty.compute, simpleProperty.type))
            } else {
                val relProperty = RelPropertyVisitor.visitExpression(ctx.expression())
                setOf(ModelProperty(propertyName, relProperty.m.readonly, relProperty.m.compute, relProperty.m.type, relProperty.target))
            }
        } else {
            return emptySet()
        }
    }

    override fun defaultResult() = emptySet<ModelProperty>()
    override fun aggregateResult(aggregate: Set<ModelProperty>, nextResult: Set<ModelProperty>) = aggregate + nextResult
}

object RelPropertyVisitor : KotlinParserBaseVisitor<NewReletationFieldAntlr>() {
    override fun visitInfixFunctionCall(ctx: KotlinParser.InfixFunctionCallContext): NewReletationFieldAntlr? {
        if (ctx.SimpleName(0).text == "of") {
            val m = SimplePropertyVisitor.visitRangeExpression(ctx.rangeExpression(0))
            val m2 = RelationPropertyVisitor.visitRangeExpression(ctx.rangeExpression(1))
            return NewReletationFieldAntlr(m, m2)
        }
        return super.visitInfixFunctionCall(ctx)
    }
}

object SimplePropertyVisitor : KotlinParserBaseVisitor<NewSimpleFieldAntlr>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): NewSimpleFieldAntlr? {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        if (mName == "newfield") {
            val type = SimplePropertyVisitor.visitPostfixUnaryOperation(ctx.postfixUnaryOperation(0))
            return NewSimpleFieldAntlr(false, FieldTypeCompute.NEWFIELD, type.type)
        }
        if (mName == "compute") {
            val type = SimplePropertyVisitor.visitCallSuffix(ctx.callSuffix())
            return NewSimpleFieldAntlr(type.readonly, FieldTypeCompute.COMPUTE, type.type)
        }
        if (mName == "computeStore") {
            val type = SimplePropertyVisitor.visitCallSuffix(ctx.callSuffix())
            return NewSimpleFieldAntlr(type.readonly, FieldTypeCompute.COMPUTESTORE, type.type)
        }
        return null
    }

    override fun defaultResult() = null
    override fun shouldVisitNextChild(node: RuleNode, currentResult: NewSimpleFieldAntlr?) = currentResult == null
}

object RelationPropertyVisitor : KotlinParserBaseVisitor<Pair<String, String>>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): Pair<String, String>? {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        var col = ""
        if (!ctx.postfixUnaryOperation().isEmpty()) {
            col = GetAtomicExpressionName.visitPostfixUnaryOperation(ctx.postfixUnaryOperation(0))
        }
        return Pair(mName, col)
    }

    override fun defaultResult() = Pair("", "")
    override fun shouldVisitNextChild(node: RuleNode, currentResult: Pair<String, String>) = currentResult == defaultResult()
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