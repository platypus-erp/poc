package org.platypus.modules.parser.visitor

import org.antlr.v4.runtime.tree.RuleNode
import org.platypus.modules.data.Model
import org.platypus.modules.data.ParseResult
import org.platypus.modules.data.ParseResultObject
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.lang.kotlin.KotlinParserBaseVisitor

/**
 * @author chmuchme
 * @since 0.1
 * on 29/08/17.
 */
val KotlinParser.ImportHeaderContext.statement: String
    get() = this.identifier().joinToString(".") {
        it.SimpleName().text
    }

val KotlinParser.ImportHeaderContext.statementByKey: Pair<String, String>
    get() = Pair(this.identifier(this.identifier().size - 1).SimpleName().text, this.identifier().joinToString(".") {
        it.SimpleName().text
    })

val KotlinParser.PackageHeaderContext.statement: String
    get() = this.identifier().joinToString(".") {
        it.SimpleName().text
    }

object FileParser : KotlinParserBaseVisitor<ParseResult>() {

    override fun visitPackageHeader(ctx: KotlinParser.PackageHeaderContext): ParseResult {
        return ParseResult(packageModel = ctx.statement)
    }

//    override fun visitImportHeader(ctx: KotlinParser.ImportHeaderContext): ParseResult {
//        val (className, import) = ctx.statementByKey
//        return ParseResult(imports = mutableMapOf(Pair(className, import)))
//    }

    override fun visitObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext): ParseResult {
        val res = RootModelVisitor.visitObjectDeclaration(ctx)
        return if (res != null) ParseResult(models = mutableSetOf(res.model), errors = res.errors) else ParseResult()
    }

    override fun visitToplevelObject(ctx: KotlinParser.ToplevelObjectContext): ParseResult {
        val obj = ctx.objectDeclaration()
        return if (obj != null) this.visitObjectDeclaration(obj) else ParseResult()
    }

    override fun defaultResult(): ParseResult {
        return ParseResult()
    }

    override fun aggregateResult(aggregate: ParseResult, nextResult: ParseResult?): ParseResult {
        return if (nextResult != null) {
            nextResult.errors += aggregate.errors
            nextResult.models += aggregate.models
            nextResult.imports += aggregate.imports
            if (nextResult.packageModel.isBlank()) {
                nextResult.packageModel = aggregate.packageModel
            }
            nextResult
        } else {
            aggregate
        }

    }
}

object GetAtomicExpressionName : KotlinParserBaseVisitor<String>() {
    override fun visitAtomicExpression(ctx: KotlinParser.AtomicExpressionContext) = ctx.identifier().SimpleName().text
    override fun defaultResult() = ""
    override fun shouldVisitNextChild(node: RuleNode, currentResult: String) = currentResult.isBlank()


}

object RootModelVisitor : KotlinParserBaseVisitor<ParseResultObject>() {

    override fun visitObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext): ParseResultObject? {
        val gg = ctx.supertypesSpecifiers()
        return if (gg != null && ValidatorObject.visitSupertypesSpecifiers(gg)) {
            println("Parsing ${ctx.SimpleName().text}")
            ParseResultObject(
                    Model(
                            ctx.SimpleName().text,
                            MethodVisitor.visitObjectDeclaration(ctx),
                            PropertyVisitor.visitObjectDeclaration(ctx),
                            true
                    ),
                    mutableSetOf())
        } else {
            null
        }
    }

    override fun shouldVisitNextChild(node: RuleNode?, currentResult: ParseResultObject?): Boolean {
        return !(node is KotlinParser.ObjectDeclarationContext && currentResult == null)
    }
}

object ValidatorObject : KotlinParserBaseVisitor<Boolean>() {

    override fun visitSupertypesSpecifiers(ctx: KotlinParser.SupertypesSpecifiersContext): Boolean {
        return ctx.delegationSpecifier()
                ?.first()
                ?.constructorInvocation()
                ?.userType()
                ?.simpleUserType()
                ?.first()
                ?.SimpleName()
                ?.text ?: "no_valid" == "Model"
    }

    override fun defaultResult(): Boolean {
        return false
    }

    override fun shouldVisitNextChild(node: RuleNode?, currentResult: Boolean?): Boolean {
        return currentResult ?: true
    }

    override fun aggregateResult(aggregate: Boolean, nextResult: Boolean?): Boolean {
        return aggregate || (nextResult ?: false)
    }
}


object PropertyTargetVisitor : KotlinParserBaseVisitor<String>() {
    override fun visitAtomicExpression(ctx: KotlinParser.AtomicExpressionContext): String? {
        return ctx.identifier()?.SimpleName()?.text ?: super.visitAtomicExpression(ctx)
    }

    override fun visitInfixFunctionCall(ctx: KotlinParser.InfixFunctionCallContext): String? {
        var target = if (ctx.SimpleName().isNotEmpty()) ctx.SimpleName()[0].text else "no_target"
        if (target == "of") {
            target = ctx.rangeExpression().last()
                    .additiveExpression().first()
                    .multiplicativeExpression().first()
                    .typeRHS().first()
                    .prefixUnaryExpression().first()
                    .postfixUnaryExpression().atomicExpression().text
            val col = ctx.rangeExpression().last()
                    .additiveExpression().first()
                    .multiplicativeExpression().first()
                    .typeRHS().first()
                    .prefixUnaryExpression().first()
                    .postfixUnaryExpression()
                    .postfixUnaryOperation().firstOrNull()
                    ?.postfixUnaryExpression()?.atomicExpression()?.identifier()?.SimpleName()?.text ?: "no_col"
            if (col != "no_col") {
                target += ".$col"
            }
        }
        return target
    }

    override fun defaultResult(): String {
        return ""
    }

    override fun shouldVisitNextChild(node: RuleNode, currentResult: String): Boolean {
        return currentResult != "no_infix"
    }
}
