package org.platypus.modules.data

import com.squareup.kotlinpoet.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedIterable
import org.platypus.core.orm.PlatypusEntity
import org.platypus.core.orm.PlatypusEntityClass
import org.platypus.core.orm.PlatypusTable
import org.platypus.core.orm.methods.*
import org.platypus.modules.firstUpper
import java.math.BigDecimal
import java.sql.Clob
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KClass

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */
val platypusEntity = org.platypus.core.orm.PlatypusEntity::class.asClassName()
val platypusEntityClass = org.platypus.core.orm.PlatypusEntityClass::class.asClassName()
val sizedIterator = SizedIterable::class.asClassName()
val entityId = EntityID::class.asParameterType(Long::class)

fun KClass<*>.asParameterType(vararg kClass: KClass<*>) = ParameterizedTypeName.get(this, *kClass)
fun KClass<*>.asParameterType(vararg kClass: TypeName) = ParameterizedTypeName.get(this.asClassName(), *kClass)
fun ClassName.asParameterType(vararg kClass: TypeName) = ParameterizedTypeName.get(this, *kClass)
fun ClassName.asNullable(b: Boolean) = if (b) this.asNullable() else this.asNonNullable()

val stringClass = String::class.asClassName()
val dateClass = LocalDate::class.asClassName()
val datetimeClass = LocalDateTime::class.asClassName()
val timeClass = LocalTime::class.asClassName()
val booleanClass = Boolean::class.asClassName()
val integerClass = Int::class.asClassName()
val floatClass = Float::class.asClassName()
val decimalClass = BigDecimal::class.asClassName()
val platypusTable = PlatypusTable::class.asClassName()
val platypusEntiy = PlatypusEntity::class.asClassName()
val platypusEntiyClass = PlatypusEntityClass::class.asClassName()
val stringColumn = Column::class.asParameterType(String::class)
val columnEntityLong = Column::class.asParameterType(EntityID::class.asParameterType(Long::class))
val dateColumn = Column::class.asParameterType(LocalDate::class)
val datetimeColumn = Column::class.asParameterType(LocalDateTime::class)
val timeColumn = Column::class.asParameterType(LocalTime::class)
val textColumn = Column::class.asParameterType(String::class)
val binaryColumn = Column::class.asParameterType(Clob::class)
val integerColumn = Column::class.asParameterType(Int::class)
val floatColumn = Column::class.asParameterType(Float::class)
val decimalColumn = Column::class.asParameterType(BigDecimal::class)
val booleanColumn = Column::class.asParameterType(Boolean::class)
val selectionColumn = Column::class.asParameterType(String::class)

enum class SimplePropertyType(val simple: Boolean,
                              val templateGenerateTable: (prop: ModelProperty, model: ClassName) -> PropertySpec.Builder,
                              val templateGenerateEntity: (prop: ModelProperty, model: ClassName) -> PropertySpec.Builder) {
    STRING(
            true,
            { f, model ->
                PropertySpec.builder(f.name, stringColumn)
                        .initializer("stringColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                PropertySpec.builder(f.name, stringClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }
    ),
    BOOLEAN(true,
            { f, model ->
                PropertySpec.builder(f.name, booleanColumn)
                        .initializer("booleanColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                PropertySpec.builder(f.name, booleanClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }),
    DATE(true,
            { f, model ->
                PropertySpec.builder(f.name, dateColumn)
                        .initializer("dateColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                PropertySpec.builder(f.name, dateClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }),
    DATETIME(true,
            { f, model ->
                PropertySpec.builder(f.name, datetimeColumn)
                        .initializer("datetimeColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                PropertySpec.builder(f.name, datetimeClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }),
    TIME(true,
            { f, model ->
                PropertySpec.builder(f.name, timeColumn)
                        .initializer("timeColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                PropertySpec.builder(f.name, timeClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }),
    FLOAT(true,
            { f, model ->
                PropertySpec.builder(f.name, floatColumn)
                        .initializer("floatColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                PropertySpec.builder(f.name, floatClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }),
    INTEGER(true,
            { f, model ->
                PropertySpec.builder(f.name, integerColumn)
                        .initializer("integerColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                PropertySpec.builder(f.name, integerClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }),
    DECIMAL(true,
            { f, model ->
                PropertySpec.builder(f.name, decimalColumn)
                        .initializer("decimalColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                PropertySpec.builder(f.name, stringClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }),
    TEXT(true,
            { f, model ->
                PropertySpec.builder(f.name, textColumn)
                        .initializer("textColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                com.squareup.kotlinpoet.PropertySpec.builder(f.name, stringClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }),
    BINARY(true,
            { f, model ->
                PropertySpec.builder(f.name, binaryColumn)
                        .initializer("binaryColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                PropertySpec.builder(f.name, stringClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }),
    MANY2ONE(false,
            { f, model ->
                PropertySpec.builder(f.name, columnEntityLong)
                        .initializer("many2oneColumn(%S, %T.%N, %N)", f.name, model, f.name, "${f.target!!.first}Table")
            },
            { f, table ->
                val targetClass = com.squareup.kotlinpoet.ClassName("", "${f.target!!.first}Entity")
                PropertySpec.builder(f.name, targetClass)
                        .delegate("%T referencedOn  %T.%N", targetClass, table, f.name).mutable(f.readonly)
            }),
    MANY2MANY(false,
            { f, model ->
                PropertySpec.builder(f.name, columnEntityLong)
                        .initializer("many2manyColumn(%S, %T.%N, %N)", f.name, model, f.name, f.target!!.first)
            },
            { f, table ->
                PropertySpec.builder(f.name, stringColumn).initializer("%S //TODO", "")
            }),
    ONE2MANY(false,
            { f, model ->
                PropertySpec.builder(f.name, columnEntityLong)
                        .initializer("one2manyColumn(%S, %T.%N, %N.%N)", f.name, model, f.name, "${f.target!!.first}Table", f.target.second)
            },
            { f, table ->
                val targetClass = ClassName("", "${f.target!!.first}Entity")
                PropertySpec.builder(f.name, sizedIterator.asParameterType(targetClass))
                        .delegate("%T referrersOn  %T.%N", targetClass, table, f.name).mutable(f.readonly)
            }),
    SELECTION(true,
            { f, model ->
                PropertySpec.builder(f.name, selectionColumn)
                        .initializer("selectionColumn(%S, %T.%N)", f.name, model, f.name)
            },
            { f, table ->
                PropertySpec.builder(f.name, stringClass.asNullable(f.required))
                        .delegate("%T.%N", table, f.name).mutable(f.readonly)
            }),
    NONE(false,
            { f, model -> throw IllegalStateException() },
            { f, table -> throw IllegalStateException() })
}

enum class FieldTypeCompute {
    NEWFIELD, COMPUTE, COMPUTESTORE, NONE
}

enum class MethodType(val noReturn: KClass<*>, val withReturn: KClass<*>) {
    MULTI(MultiMethodResultNoReturn::class, MultiMethodResultWithReturn::class),
    NONE(Void::class, Void::class),
    GROUP(Void::class, Void::class)
}

data class ParseResult(var packageModel: String = "",
                       val models: MutableSet<ModelGenerate> = mutableSetOf(),
                       val imports: MutableMap<String, String> = mutableMapOf(),
                       val errors: MutableSet<String> = mutableSetOf())

data class ParseResultObject(val model: ModelGenerate, val errors: MutableSet<String>)


data class RootModel(val name: String,
                     val properties: Set<ModelProperty>)

data class ModelProperty(val name: String,
                         val readonly: Boolean,
                         val required: Boolean,
                         val compute: FieldTypeCompute,
                         val type: SimplePropertyType,
                         val target: Pair<String, String>? = null)

data class ModelMethod(val name: String,
                       val type: MethodType,
                       val paramType: String,
                       val returnType: String)

open class ModelGenerate(
        val pkg: String,
        val name: String,
        val method: Set<ModelMethod> = mutableSetOf(),
        val simpleField: Set<ModelProperty> = mutableSetOf(),
        val root: Boolean = true)

class M2MModel(modelName: String,
               fieldName: String,
               targetModelName: String,
               targetFieldName: String) :
        ModelGenerate("",
                modelName.firstUpper() + fieldName.firstUpper() + targetModelName.firstUpper() + "${targetFieldName.firstUpper()}Rel",
                simpleField = setOf(
                        ModelProperty(fieldName, false, false, FieldTypeCompute.NEWFIELD, SimplePropertyType.MANY2ONE, Pair(targetModelName, "")),
                        ModelProperty(targetFieldName, false, false, FieldTypeCompute.NEWFIELD, SimplePropertyType.MANY2ONE, Pair(modelName, ""))
                )
        ) {
    val field1 = super.simpleField.first()
    val field2 = super.simpleField.last()
}
