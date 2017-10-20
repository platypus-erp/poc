package org.platypus.modules.generator.orm

import com.squareup.kotlinpoet.*
import org.platypus.modules.data.ModelGenerate
import org.platypus.modules.data.entityId
import org.platypus.modules.firstUpper

/**
 * @author chmuchme
 * @since 0.1
 * on 31/08/17.
 */
val platypusEntity = org.platypus.core.orm.PlatypusEntity::class.asClassName()
val platypusEntityClass = org.platypus.core.orm.PlatypusEntityClass::class.asClassName()

data class ModelEntiy(val entity: TypeSpec.Builder, val superEntiy: TypeSpec, val superCompanion: TypeSpec)

//abstract class PartnerEntityPlatypusEntityClass : PlatypusEntityClass<PartnerEntity>(Partner, PartnerTable)
fun generateEntity(m: ModelGenerate): ModelEntiy {
    println("${m.name.firstUpper()}Entity")
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
        println("\t"+ f.name + " " + f.type)
        entityBuilder.addProperty(
                f.type.templateGenerateEntity(f, table).build()
        )
    }

    for (meth in m.method) {
        println(meth.name)
    }
    return ModelEntiy(entityBuilder, superEntiy, superCompanion)
}



