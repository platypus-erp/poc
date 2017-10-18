package org.platypus.modules.generator.orm

import com.squareup.kotlinpoet.*
import org.jetbrains.exposed.sql.Column
import org.platypus.core.orm.PlatypusEntity
import org.platypus.core.orm.PlatypusEntityClass
import org.platypus.core.orm.PlatypusTable
import org.platypus.modules.data.Model
import org.platypus.modules.data.SimplePropertyType
import org.platypus.modules.firstUpper
import org.platypus.modules.toSneakeCase
import java.math.BigDecimal
import java.sql.Clob
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

val platypusTable = PlatypusTable::class.asClassName()
val platypusEntiy = PlatypusEntity::class.asClassName()
val platypusEntiyClass = PlatypusEntityClass::class.asClassName()
val stringColumn = Column::class.asParameterType(String::class)
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


fun generateTable(m: Model): TypeSpec.Builder {
    val model = ClassName(m.pkg, m.name)

    val tableBuilder = TypeSpec.objectBuilder("${m.name.firstUpper()}Table")
            .superclass(platypusTable).addSuperclassConstructorParameter("%S", m.name.toSneakeCase())

    for (f in m.simpleField) {
        tableBuilder.addProperty(
                when (f.type) {
                    SimplePropertyType.STRING -> {
                        val prop = PropertySpec.builder(f.name, stringColumn);
                        if (f.required){
                            prop.initializer("stringColumn(%S, %T.%N)", f.name, model, f.name)
                        } else {
                            prop.initializer("stringColumn(%S, %T.%N).nullable()", f.name, model, f.name)
                        }
                        prop.build()
                    }
                    SimplePropertyType.DATE -> PropertySpec.builder(f.name, dateColumn)
                            .initializer("dateColumn(%S, %T.%N)", f.name, model, f.name)
                            .build()
                    SimplePropertyType.DATETIME -> PropertySpec.builder(f.name, datetimeColumn)
                            .initializer("datetimeColumn(%S, %T.%N)", f.name, model, f.name)
                            .build()
                    SimplePropertyType.TIME -> PropertySpec.builder(f.name, timeColumn)
                            .initializer("timeColumn(%S, %T.%N)", f.name, model, f.name)
                            .build()
                    SimplePropertyType.TEXT -> PropertySpec.builder(f.name, textColumn)
                            .initializer("textColumn(%S, %T.%N)", f.name, model, f.name)
                            .build()
                    SimplePropertyType.BINARY -> PropertySpec.builder(f.name, binaryColumn)
                            .initializer("binaryColumn(%S, %T.%N)", f.name, model, f.name)
                            .build()
                    SimplePropertyType.BOOLEAN -> PropertySpec.builder(f.name, booleanColumn)
                            .initializer("booleanColumn(%S, %T.%N)", f.name, model, f.name)
                            .build()
                    SimplePropertyType.INTEGER -> PropertySpec.builder(f.name, integerColumn)
                            .initializer("integerColumn(%S, %T.%N)", f.name, model, f.name)
                            .build()
                    SimplePropertyType.FLOAT -> PropertySpec.builder(f.name, floatColumn)
                            .initializer("floatColumn(%S, %T.%N)", f.name, model, f.name)
                            .build()
                    SimplePropertyType.SELECTION -> PropertySpec.builder(f.name, selectionColumn)
                            .initializer("selectionColumn(%S, %T.%N)", f.name, model, f.name)
                            .build()
                    SimplePropertyType.DECIMAL -> PropertySpec.builder(f.name, decimalColumn)
                            .initializer("decimalColumn(%S, %T.%N)", f.name, model, f.name)
                            .build()
                    SimplePropertyType.ONE2MANY -> PropertySpec.builder(f.name, stringColumn)
                            .initializer("one2manyColumn(%S, %T.%N, %T.%N)", f.name, model, f.name, f.target!!.first, f.target.second)
                            .build()
                    SimplePropertyType.MANY2MANY -> PropertySpec.builder(f.name, stringColumn)
                            .initializer("many2manyColumn(%S, %T.%N, %T)", f.name, model, f.name, f.target!!.first)
                            .build()
                    SimplePropertyType.MANY2ONE -> PropertySpec.builder(f.name, stringColumn)
                            .initializer("many2oneColumn(%S, %T.%N, %T)", f.name, model, f.name, f.target!!.first)
                            .build()
                    SimplePropertyType.NONE -> throw IllegalStateException()
                }
        )
    }
    return tableBuilder
}