package org.platypus.modules.generator

import com.squareup.kotlinpoet.TypeSpec
import org.platypus.modules.data.ParseResult
import org.platypus.modules.generator.orm.generateEntity
import org.platypus.modules.generator.orm.generateTable

/**
 * @author chmuchme
 * @since 0.1
 * on 14/10/17.
 */
data class GeneratorResult(val tables:Set<TypeSpec>, val entity:Set<TypeSpec>)

fun generate(res: ParseResult): GeneratorResult {
    val builderSet: MutableSet<TypeSpec> = HashSet()
    val builderEntitySet: MutableSet<TypeSpec> = HashSet()
    res.models.mapTo(builderSet) { generateTable(it).build() }
    res.models.forEach{
        val r = generateEntity(it)
        builderEntitySet.add(r.superCompanion)
        builderEntitySet.add(r.superEntiy)
        builderEntitySet.add(r.entity.build())
    }
    return GeneratorResult(builderSet, builderEntitySet)
}