package org.platypus.modules.parser

import org.platypus.modules.builder.generateEntityC
import org.platypus.modules.parser.generator.orm.exposed.EntityGenerator
import java.io.File
import java.nio.file.Paths

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */
class ParserTest
fun main(args: Array<String>) {
    val res = ParserFacade.getKotlinFileFromStream(ParserTest::class.java.getResourceAsStream("test.ktest"))
    println(res)
//    val file = EntityGenerator.generateEntitys(res.packageModel, res.models)
//
//    print(file)
}