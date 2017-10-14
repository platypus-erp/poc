package org.platypus.modules.parser

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RuleContext
import org.platypus.modules.data.ParseResult
import org.platypus.modules.lang.kotlin.KotlinLexer
import org.platypus.modules.lang.kotlin.KotlinParser
import org.platypus.modules.parser.visitor.FileParser
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author chmuchme
 * @since 0.1
 * on 01/09/17.
 */
object ParserFacade {

    private fun readFile(file: File, encoding: Charset): String {
        val encoded = Files.readAllBytes(file.toPath())
        return String(encoded, encoding)
    }

    fun getKotlinFile(path: Path):KotlinParser.KotlinFileContext{
        val code = readFile(path.toFile(), Charset.forName("UTF-8"))
        println(path)
        val lexer = KotlinLexer(ANTLRInputStream(code))
        val tokens = CommonTokenStream(lexer)
        val parser = KotlinParser(tokens)
        return parser.kotlinFile()
    }

    fun getKotlinFileFromStream(path: InputStream):KotlinParser.KotlinFileContext{
        val lexer = KotlinLexer(ANTLRInputStream(path))
        val tokens = CommonTokenStream(lexer)
        val parser = KotlinParser(tokens)
        return parser.kotlinFile()
    }

    fun parse(path: Path): ParseResult = FileParser.visit(getKotlinFile(path))
    fun print(path: Path) = AstPrinter.visit(getKotlinFile(path))

}

object ModelsParser {
    fun run(modelDir: Path): ParseResult {
        val kotlinMatcher = FileSystems.getDefault().getPathMatcher("glob:**.kt")
        val result: Set<ParseResult> = Stream.of(modelDir)
                .flatMap<Path>({ walkPath(it) })
                .filter { p -> !Files.isDirectory(p) }
                .filter({ kotlinMatcher.matches(it) })
                .map({ ParserFacade.parse(it) })
                .collect(Collectors.toSet<ParseResult>())
//        val path = Paths.get("/home/chmuchme/WorkSpace/KOTLIN/Kassiopeia/core/src/main/kotlin/org/platypus/modules/base/usersff.kt")
//        val result = mutableSetOf(ParserFacade.parse(path))
        val finalResult = ParseResult()
        for ((packageModel, models, imports, errors) in result.filter { it.models.isNotEmpty() }) {
            finalResult.imports += imports
            finalResult.imports.put(packageModel, "${packageModel}.*")
            finalResult.models += models
            finalResult.errors += errors
        }
        return finalResult
    }

    private fun walkPath(p: Path): Stream<Path> {
        try {
            return Files.walk(p)
        } catch (e: IOException) {
            throw IllegalArgumentException(e)
        }

    }
}

object AstPrinter {

    fun visit(ctx: RuleContext): ParseResult {
        explore(ctx, 0)
        return ParseResult()
    }

    private fun explore(ctx: RuleContext, indentation: Int) {
        val toBeIgnored = ctx.childCount == 1 && ctx.getChild(0) is ParserRuleContext
        if (!toBeIgnored) {
            val ruleName = KotlinParser.ruleNames[ctx.ruleIndex]
            for (i in 0..indentation - 1) {
                print("  ")
            }
            println(ruleName)
            for (i in 0..indentation - 1) {
                print("  ")
            }
            println(ctx.text)
        }
        for (i in 0..ctx.childCount - 1) {
            val element = ctx.getChild(i)
            if (element is RuleContext) {
                explore(element, indentation + if (toBeIgnored) 0 else 1)
            }
        }
    }

}