package org.platypus.modules.builder

import org.platypus.modules.parser.generator.orm.exposed.EntityGenerator
import org.platypus.modules.parser.ModelsFinder
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path


/**
 * @author chmuchme
 * @since 0.1
 * on 01/09/17.
 */
val incremetalFile = "incr.kasiop"

interface Command {

    operator fun  invoke(path: Path, args: Array<String> = emptyArray())
}

val commands = mutableMapOf<String, Command>(
        Pair("help", helpC),
        Pair("full", fullC),
        Pair("generateEntity", generateEntityC),
        Pair("init", initC),
        Pair("generate", generateC)
)

fun main(args: Array<String>) {
    with("toto"){
        print(plus("asdasd"))
    }
}



//fun main(args: Array<String>) {
//    val path = Paths.get(Paths.get("").toAbsolutePath().toString(),"core")
//    if (args.size > 0) {
//        args[0]
//    }
//    val res = ParserFacade.parse(Paths.get("/home/chmuchme/WorkSpace/KOTLIN/Kassiopeia/core/src/main/kotlin/org/platypus/modules/base/users.kt"))
////    val res = parserFacade.parse(File("examples/test.kt"))
//    println(res)
//    val file = generateEntitys(res.packageModel, res.models)
//
//    print(file)
//    generateEntityC(path)
//}

object initC :Command {
    override operator fun invoke(path: Path, args: Array<String>) {


    }

}

object helpC :Command {
    override operator fun invoke(path: Path, args: Array<String>) {

    }

}
object fullC :Command {
    override operator fun invoke(path: Path, args: Array<String>) {
        cleanC(path, args)
        importsC(path, args)
        generateC(path, args)
    }

}
object generateC :Command {
    override operator fun invoke(path: Path, args: Array<String>) {

    }

}
object generateEntityC :Command {
    override operator fun invoke(path: Path, args: Array<String>) {
        if(!Files.exists(path.resolve("modules"))){
            Files.createDirectory(path.resolve("modules"))
            Files.createFile(path.resolve("modules").resolve(incremetalFile))
        }
        val models = ModelsFinder.run(path.resolve("src").resolve("main").resolve("kotlin").resolve("org")
                .resolve("platypus").resolve("modules").resolve("base"))
        if (models.errors.isNotEmpty()){
            println("-------------Errors detected inside your code-------------")
            models.errors.forEach {
                println(it)
            }
            return
        }
        cleanC(path, args)
        val pathEntitySrc = path
                .resolve("src")
                .resolve("main")
                .resolve("kotlin")
                .resolve("modules")
                .resolve("entity")
        Files.deleteIfExists(pathEntitySrc.resolve("entity.kt"))
        val file = Files.createFile(pathEntitySrc.resolve("entity.kt"))
        Files.write(file, EntityGenerator.generateEntitys("modules.entity", models.models, models.imports.values)
                .toByteArray(Charset.forName("UTF-8")))

    }

}

object generateKassiopiaJsonC :Command {
    override operator fun invoke(path: Path, args: Array<String>) {
    }

}
object importsC :Command {
    override operator fun invoke(path: Path, args: Array<String>) {
    }

}
object cleanC :Command {
    override operator fun invoke(path: Path, args: Array<String>) {
    }

}
