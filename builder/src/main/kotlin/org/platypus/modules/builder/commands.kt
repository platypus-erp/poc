package org.platypus.modules.builder

import com.squareup.kotlinpoet.FileSpec
import org.platypus.modules.generator.generate
import org.platypus.modules.parser.ModelsParser
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * @author chmuchme
 * @since 0.1
 * on 01/09/17.
 */
fun launch(mainArgs: Array<String>) {
    val param = mainArgs.map {
        val sr = it.split("=")
        Pair(sr[0], sr[1])
    }.toMap()

    val group = param["--group"]!!
    val artifactId = param["--artifactId"]!!
    val modelsPath = param["--models"] ?: "models"

    val modulePath = Paths.get("").toAbsolutePath().resolve(artifactId)
    assert(Files.exists(modulePath)) {
        "The module don't exist. path=$modulePath"
    }
    val pathSrc = getMainPath(artifactId, group)
    println(pathSrc)
    assert(!Files.exists(pathSrc)) {
        "The module src don't exist. path=$pathSrc"
    }
    val viewsPath = pathSrc.resolve(param["--views"] ?: "views")
    assert(Files.exists(viewsPath)) {
        "The module src don't exist. path=$pathSrc"
    }
    val datasPath = pathSrc.resolve(param["--datas"] ?: "datas")
    assert(Files.exists(datasPath)) {
        "The module src don't exist. path=$pathSrc"
    }
    val securityPath = pathSrc.resolve(param["--security"] ?: "security")
    assert(Files.exists(securityPath)) {
        "The module src don't exist. path=$pathSrc"
    }
    val pathGenerate = modulePath.resolve(param["--gen"] ?: "build/generated-src")
    assert(Files.exists(pathGenerate)) {
        "The module src don't exist. path=$pathSrc"
    }


    val res = ModelsParser.run(pathSrc.resolve(modelsPath))


    if (res.errors.isNotEmpty()) {
        println("-------------Errors detected inside your code-------------")
        res.errors.forEach {
            println(it)
        }
        return
    }


    val pathEntitySrc = pathGenerate.resolve(group.replace(".", "/")).resolve("entity")
    val pathTablesSrc = pathGenerate.resolve(group.replace(".", "/")).resolve("tables")

//    val bigFile = EntityGenerator.generateEntitys("modules.entity", res.models, res.imports.values)

    val tableFile = FileSpec.builder("$group.$artifactId.models", "${artifactId}Table")
    generate(res).tables.forEach { tableFile.addType(it) }
    val entityFile = FileSpec.builder("$group.$artifactId.models", "${artifactId}Entity")
    generate(res).entity.forEach { entityFile.addType(it) }


//    Files.deleteIfExists(pathEntitySrc.resolve("entity.kt"))
//    val file = Files.createFile(pathEntitySrc.resolve("entity.kt"))
//    Files.write(file, bigFile.toByteArray(Charset.forName("UTF-8")))
    tableFile.build().writeTo(pathGenerate)
    entityFile.build().writeTo(pathGenerate)
}

private fun getMainPath(artifactId: String, group: String): Path {
    return Paths.get("").toAbsolutePath()
            .resolve(artifactId)
            .resolve("src").resolve("main").resolve("kotlin")
            .resolve(group.replace(".", "/")).resolve(artifactId)
}

object GenerateEntityAndTable {
    fun run(pathGenerate: String, artifactId: String, group: String, path: Path) {


    }

}
