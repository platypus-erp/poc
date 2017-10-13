package org.platypus.modules.parser.generator.orm.exposed

import org.platypus.modules.data.Model
import org.platypus.modules.data.ModelField
import org.platypus.modules.parser.generator.M2MRegistry
import org.platypus.modules.parser.generator.ModuleModelOrganiser
import org.platypus.modules.parser.types
import org.platypus.modules.parser.visitor.FieldType

/**
 * @author chmuchme
 * @since 0.1
 * on 31/08/17.
 */
val importbase = """
import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonArray
import org.platypus.core.AppCtx
import org.platypus.core.AppEntity
import org.platypus.core.orm.KassiopiaEntity
import org.platypus.core.orm.KassiopiaIntEntityClass
import org.platypus.core.orm.TableRegistry
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.math.BigDecimal
import java.time.LocalDateTime
import org.platypus.core.orm.Many2ManyModel
"""
var template = """
/**
 * @author kassiopiaGenerator
 * Generated Class for model inside
 */

sealed class KassiopiaGeneratedEntity(id: EntityID<Int>) : KassiopiaEntity(id)
"""

infix fun <T : Any> String.pair(s: T): Pair<String, T> {
    return Pair(this, s)
}

object EntityGenerator {

    fun generateEntitys(packageModel: String, m: Set<Model>, imports: MutableCollection<String>): String {
        val org = ModuleModelOrganiser(m)
        org.resolveM2M()

        var file = "package $packageModel\n"
        file += importbase
        imports.forEach {
            file += "imports $it\n"
        }
        file += M2MRegistry.generateM2M()
        file += template
        m.forEach {
            file += EntityGenerator.generateEntity(it)
        }
        return file
    }

    private fun generateEntity(m: Model): String {
        val fieldToJson = m.method
                .filter { it.ftype == FieldType.FIELD }
                .joinToString(separator = ",\n                ") {
                    when (it.type) {
                        "many2one" -> {
                            "${m.name}.${it.name}.name to ${it.name}.toJson()"
                        }
                        "many2many" -> {
                            "//${m.name}.${it.name}.name to JsonArray(${it.name})"
                        }
                        "one2many" -> {
                            "${m.name}.${it.name}.name to JsonArray(${it.name})"
                        }
                        else -> {
                            "${m.name}.${it.name}.name to ${it.name}"
                        }
                    }

                }


        var templateClass = "\n\n" + """
class ${m.name}Entity(id: EntityID<Int>,val ctx: AppCtx) : KassiopiaGeneratedEntity(id) {
    companion object : KassiopiaIntEntityClass<${m.name}Entity>(${m.name}){
        override fun getInstanceWithContext(entityId: EntityID<Int>, row: ResultRow?, ctx: AppCtx): ${m.name}Entity {
            return ${m.name}Entity(entityId, ctx)
        }

        override fun withCtx(ctx: AppCtx): ${m.name}Entity.Companion {
            super.withCtx(ctx)
            return this
        }

        val AppEntity.${m.name.toLowerCase()}: ${m.name}Entity.Companion
            get() = ${m.name}Entity.Companion.withCtx(this.ctx)

        init {
            TableRegistry.addTable(this)
        }
    }
    override fun makeCopy(deep: Boolean): ${m.name}Entity {
        return this
    }

    override fun toJson(): JsonObject {
        return JsonObject(mapOf(
                $fieldToJson
            )
        )
    }"""
        for (f in m.method) {
            templateClass += "\n    " + when (f.ftype) {
                FieldType.FIELD -> generateField(m.name, f)
                FieldType.ON_CHANGE -> generateOnChange(m.name, f)
                FieldType.COMPUTE_GET -> TODO()
                FieldType.COMPUTE_SET -> TODO()
                FieldType.METHOD_MULTI -> TODO()
                FieldType.METHOD_ONE -> TODO()
                FieldType.METHOD_STATIC -> TODO()
            }
        }
        templateClass += "\n}"
        return templateClass
    }

    private fun generateField(modelName: String, m: ModelField): String {
        var templateClass = ""

        val targetReplace = m.target.replace("this", modelName)
        val rtype = types[m.type] ?: m.type

        when (m.type) {
            "many2one" -> {
                templateClass += "var ${m.name}: ${targetReplace}Entity by ${targetReplace}Entity referencedOn $modelName.${m.name}"
            }
            "many2many" -> {
                val targetWithoutCol = targetReplace.split(".")[0]
                val col = targetReplace.split(".")[1]
                val m2m = M2MRegistry.getElement(modelName, m.name, targetWithoutCol, col)
                templateClass += "val ${m.name}: SizedIterable<${targetWithoutCol}Entity> by ${targetWithoutCol}Entity via ${m2m.name}.table"
            }
            "one2many" -> {
                val targetWithoutCol = targetReplace.split(".")[0]
                templateClass += "val ${m.name}: SizedIterable<${targetWithoutCol}Entity> by ${targetWithoutCol}Entity referrersOn $targetReplace"
            }
            else -> {
                templateClass += "var ${m.name}: $rtype by $modelName.${m.name}"
            }
        }

        return templateClass
    }

    private fun generateOnChange(modelName: String, m: ModelField): String {
        val struct = """fun onChange${m.name.firstLetterUpper()}(): ${modelName}Entity {
        var entityCopy = this.makeCopy(true)
        transaction {
            val def = $modelName.onChangeLogin
            ctx.toOnchangeContext(def).Super(entityCopy)
            //TODO Get field changed
            for (p in def.changedColumn){

            }
            this.rollback()
        }
        return entityCopy
    }"""
        return struct
    }

}



