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

data class NewSimpleFieldAntlr(val readonly: Boolean, val required: Boolean, val compute: FieldTypeCompute, val type: SimplePropertyType)

data class NewReletationFieldAntlr(val m: NewSimpleFieldAntlr, val target: Pair<String, String>)

object PropertyVisitor : KotlinParserBaseVisitor<Set<ModelProperty>>() {

    override fun visitPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext): Set<ModelProperty> {
        if (ctx.expression() != null) {
            val propertyName = ctx.variableDeclarationEntry().text

            if (ctx.modifiers().text != "public") {
                //error modifier should be public on none
            }
            if (ctx.multipleVariableDeclarations() != null) {
                // error only on field par line
            }
            val isField = SimplePropertyValidator.visitExpression(ctx.expression())
            return if (isField) {
                val simpleProperty = SimplePropertyVisitor.visitExpression(ctx.expression())
                if (simpleProperty.type != SimplePropertyType.NONE){
                    return if (simpleProperty.type.simple) {
                        println("$propertyName, ${simpleProperty.type}")
                        setOf(ModelProperty(propertyName, simpleProperty.readonly, simpleProperty.required, simpleProperty.compute, simpleProperty.type))
                    } else {
                        print(propertyName)
                        val relProperty = RelPropertyVisitor.visitExpression(ctx.expression())
                        println(", " + relProperty.m.type)
                        setOf(ModelProperty(propertyName, relProperty.m.readonly, relProperty.m.required, relProperty.m.compute, relProperty.m.type, relProperty.target))
                    }
                }
                emptySet()
            } else {
                emptySet()
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
        if (ctx.SimpleName().isNotEmpty() && ctx.SimpleName(0).text == "of") {
            val m = SimplePropertyVisitor.visitRangeExpression(ctx.rangeExpression(0))
            val m2 = RelationPropertyVisitor.visitRangeExpression(ctx.rangeExpression(1))
            return NewReletationFieldAntlr(m, m2)
        }
        return super.visitInfixFunctionCall(ctx)
    }
}

object SimplePropertyValidator : KotlinParserBaseVisitor<Boolean>() {
    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): Boolean {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        val valid = mName == "newfield" || mName == "compute" || mName == "computeStore"
        if (valid){
            print("\t $mName : ")
        }
        return valid
    }

    override fun defaultResult() = false
    override fun shouldVisitNextChild(node: RuleNode, currentResult: Boolean): Boolean {
        return !currentResult
    }
}

object SimplePropertyVisitor : KotlinParserBaseVisitor<NewSimpleFieldAntlr>() {

    override fun visitPostfixUnaryExpression(ctx: KotlinParser.PostfixUnaryExpressionContext): NewSimpleFieldAntlr {
        val mName = GetAtomicExpressionName.visitAtomicExpression(ctx.atomicExpression())
        if (mName == "newfield") {
            val type = GetNewFieldType.visitPostfixUnaryOperation(ctx.postfixUnaryOperation(0))
            return NewSimpleFieldAntlr(false, false, FieldTypeCompute.NEWFIELD, type)
        }
        if (mName == "compute") {
            val type = SimplePropertyVisitor.visitCallSuffix(ctx.callSuffix())
            return NewSimpleFieldAntlr(type.readonly, type.required, FieldTypeCompute.COMPUTE, type.type)
        }
        if (mName == "computeStore") {
            val type = SimplePropertyVisitor.visitCallSuffix(ctx.callSuffix())
            return NewSimpleFieldAntlr(type.readonly, type.required, FieldTypeCompute.COMPUTESTORE, type.type)
        }
        return NewSimpleFieldAntlr(false, false, FieldTypeCompute.NONE, SimplePropertyType.NONE)
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
        return SimplePropertyType.valueOf(mName.toUpperCase())
    }

    override fun defaultResult() = SimplePropertyType.NONE
    override fun shouldVisitNextChild(node: RuleNode, currentResult: SimplePropertyType) = currentResult == SimplePropertyType.NONE
}

object GetReadOnly : KotlinParserBaseVisitor<Boolean>() {

    override fun defaultResult() = false
    override fun shouldVisitNextChild(node: RuleNode, currentResult: Boolean) = false
}