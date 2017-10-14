package org.platypus.modules

import org.platypus.modules.parser.ModelsParser
import org.platypus.modules.generator.orm.EntityGenerator
import java.nio.file.Paths

/**
 * @author chmuchme
 * @since 0.1
 * on 14/10/17.
 */
fun main(args: Array<String>) {
//    val path = Paths.get(Paths.get("").toAbsolutePath().toString(),"core")
    val path = Paths.get(Paths.get("").toAbsolutePath().toString(),"core", "src", "main", "kotlin", "sample")
    if (args.size > 0) {
        args[0]
    }
    val res = ModelsParser.run(path)
//    val res = parserFacade.parse(File("examples/test.kt"))
    println(res)
    val file = EntityGenerator.generateEntitys(res.packageModel, res.models, res.imports.values)

//    print(file)
//    generateEntityAndTable(path)
}

fun String.firstUpper():String{
    return this.first().toUpperCase() + this.substring(1)
}
private val REGEX = "([a-z])([A-Z]+)".toRegex()
fun String.toSneakeCase():String{
    return this.replace(REGEX, "$1_$2").toLowerCase()
}