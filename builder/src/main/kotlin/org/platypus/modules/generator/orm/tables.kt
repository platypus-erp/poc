package org.platypus.modules.generator.orm

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import org.platypus.modules.data.ModelGenerate
import org.platypus.modules.data.platypusTable
import org.platypus.modules.firstUpper
import org.platypus.modules.toSneakeCase


fun generateTable(m: ModelGenerate): TypeSpec.Builder {
    val model = ClassName(m.pkg, m.name)
    val table = ClassName(m.pkg,"${m.name.firstUpper()}Table")
    println("${m.name.firstUpper()}Table")
    val tableBuilder = TypeSpec.objectBuilder(table)
            .superclass(platypusTable).addSuperclassConstructorParameter("%S", m.name.toSneakeCase())

    for (f in m.simpleField) {
        println("\t"+ f.name + " " + f.type)
        tableBuilder.addProperty(f.type.templateGenerateTable(f, model).build())
    }
    return tableBuilder
}