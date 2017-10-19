package org.platypus.modules.generator.orm

import com.squareup.kotlinpoet.*
import org.jetbrains.exposed.dao.EntityID
import org.platypus.core.orm.PlatypusEntity
import org.platypus.modules.data.Model
import org.platypus.modules.data.SimplePropertyType
import org.platypus.modules.firstUpper
import org.platypus.modules.toSneakeCase
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author chmuchme
 * @since 0.1
 * on 31/08/17.
 */
val platypusEntity = org.platypus.core.orm.PlatypusEntity::class.asClassName()
val platypusEntityClass = org.platypus.core.orm.PlatypusEntityClass::class.asClassName()
val entityId = EntityID::class.asParameterType(Long::class)
val stringClass = String::class.asClassName()
val dateClass = LocalDate::class.asClassName()
val datetimeClass = LocalDateTime::class.asClassName()
val timeClass = LocalTime::class.asClassName()
val booleanClass = Boolean::class.asClassName()
val integerClass = Int::class.asClassName()
val floatClass = Float::class.asClassName()
val decimalClass = BigDecimal::class.asClassName()

data class ModelEntiy(val entity: TypeSpec.Builder, val superEntiy: TypeSpec, val superCompanion: TypeSpec)

//abstract class PartnerEntityPlatypusEntityClass : PlatypusEntityClass<PartnerEntity>(Partner, PartnerTable)
fun generateEntity(m: Model): ModelEntiy {
    val model = ClassName(m.pkg, m.name)
    val table = ClassName(m.pkg, "${m.name.firstUpper()}Table")

    val superEntiy = TypeSpec.classBuilder("${m.name.firstUpper()}EntityPlatypusEntity")
            .addModifiers(KModifier.ABSTRACT)
            .primaryConstructor(FunSpec.constructorBuilder().addParameter(ParameterSpec.builder("id", entityId).build()).build())
            .superclass(platypusEntity).addSuperclassConstructorParameter("%N", "id").build()

    val superCompanion = TypeSpec.classBuilder("${m.name.firstUpper()}EntityPlatypusEntityClass")
            .superclass(
                    platypusEntityClass.asParameterType(
                            ClassName("", "${m.name.firstUpper()}Entity")
                    )
            ).addModifiers(KModifier.ABSTRACT)
            .addSuperclassConstructorParameter("%N, %T", m.name.firstUpper(), table).build()

    val entityBuilder = TypeSpec.classBuilder("${m.name.firstUpper()}Entity")
            .primaryConstructor(FunSpec.constructorBuilder().addParameter(ParameterSpec.builder("id", entityId).build()).build())
            .superclass(
                    ClassName("", "${m.name.firstUpper()}EntityPlatypusEntity")
            ).addSuperclassConstructorParameter("%N", "id")
            .companionObject(TypeSpec.companionObjectBuilder().superclass(
                    ClassName("", "${m.name.firstUpper()}EntityPlatypusEntityClass")
            ).build())



    for (f in m.simpleField) {
        entityBuilder.addProperty(
                when (f.type) {
                    SimplePropertyType.STRING -> {
                        PropertySpec.builder(f.name, stringClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.DATE -> {
                        PropertySpec.builder(f.name, dateClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.DATETIME -> {
                        PropertySpec.builder(f.name, datetimeClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.TIME -> {
                        PropertySpec.builder(f.name, timeClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.TEXT -> {
                        PropertySpec.builder(f.name, stringClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.BINARY -> {
                        PropertySpec.builder(f.name, stringClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.BOOLEAN -> {
                        PropertySpec.builder(f.name, booleanClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.INTEGER -> {
                        PropertySpec.builder(f.name, integerClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.FLOAT -> {
                        PropertySpec.builder(f.name, floatClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.SELECTION -> {
                        PropertySpec.builder(f.name, stringClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.DECIMAL -> {
                        PropertySpec.builder(f.name, decimalClass.asNullable(f.required))
                                .delegate("%T.%N", table, f.name).mutable(f.readonly).build()
                    }
                    SimplePropertyType.ONE2MANY -> throw IllegalStateException()
                    SimplePropertyType.MANY2MANY -> throw IllegalStateException()
                    SimplePropertyType.MANY2ONE -> throw IllegalStateException()
                    SimplePropertyType.NONE -> throw IllegalStateException()
                }
        )
    }

    for (meth in m.method) {
        println(meth.name)
    }
    return ModelEntiy(entityBuilder, superEntiy, superCompanion)
}



