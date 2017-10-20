package org.platypus.modules.parser.visitor.methods

import org.platypus.modules.data.MethodType
import org.platypus.modules.data.ModelMethod
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.lang.kotlin.KotlinParserBaseVisitor

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
            return if (MethodValidator.visitPropertyDeclaration(ctx)) {
                val meth = ApiMethodVisitor.visitExpression(ctx.expression())
                if (meth.type != MethodType.NONE) {
                    return if (meth != null) {
                        println("\tParsing method $propertyName, ${meth.type}")
                        setOf(ModelMethod(propertyName, meth.type, meth.paramType, meth.returnType))
                    } else {
                        emptySet()
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

    override fun defaultResult() = emptySet<ModelMethod>()
    override fun aggregateResult(aggregate: Set<ModelMethod>, nextResult: Set<ModelMethod>) = aggregate + nextResult
}