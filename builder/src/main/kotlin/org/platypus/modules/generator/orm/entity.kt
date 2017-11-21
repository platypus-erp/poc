package org.platypus.modules.generator.orm

import com.squareup.kotlinpoet.*
import org.platypus.core.orm.methods.*
import org.platypus.modules.data.*
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
    val model = ClassName(m.pkg, m.name)
    val entity = ClassName(m.pkg, "${m.name.firstUpper()}Entity")
    val table = ClassName(m.pkg, "${m.name.firstUpper()}Table")

    val superEntiy = TypeSpec.classBuilder("${m.name.firstUpper()}EntityPlatypusEntity")
            .addModifiers(KModifier.ABSTRACT)
            .primaryConstructor(FunSpec.constructorBuilder().addParameter(ParameterSpec.builder("id", entityId).build()).build())
            .superclass(platypusEntity).addSuperclassConstructorParameter("%N", "id").build()

    val superCompanion = TypeSpec.classBuilder("${m.name.firstUpper()}EntityPlatypusEntityClass")
            .superclass(
                    platypusEntityClass.asParameterType(entity)
            ).addModifiers(KModifier.ABSTRACT)
            .addSuperclassConstructorParameter("%N, %T", m.name.firstUpper(), table)

//    for (staticMethod in m.method.filter { it.type == MethodType.STATIC }){
//        val f = FunSpec.builder(staticMethod.name)
//        val paramType = ClassName("", staticMethod.paramType)
//        var ctxType = staticMethod.type.noReturn.asParameterType(entity, paramType)
//        f.addParameter(ParameterSpec.builder("param", paramType).build())
//        if (staticMethod.returnType.isNotBlank()){
//            val rType = ClassName("", staticMethod.returnType)
//            f.returns(rType)
//            f.addCode("return %T(%S)", rType, "")
//            ctxType = staticMethod.type.withReturn.asParameterType(entity, paramType, rType)
//        }
//
//        f.addParameter(ParameterSpec.builder("ctx", ctxType).build())
//        superCompanion.addFunction(f.build())
//    }

    val entityBuilder = TypeSpec.classBuilder(entity)
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
                f.type.templateGenerateEntity(f, table).mutable(f.type != SimplePropertyType.ONE2MANY).build()
        )
    }

    for (method in m.method.filter { /*it.type != MethodType.STATIC &&*/ it.type != MethodType.NONE && it.type != MethodType.GROUP}){
        val f = FunSpec.builder(method.name)
        val paramType = ClassName("", method.paramType)
        var ctxType = method.type.noReturn.asParameterType(entity, paramType)
        f.addParameter(ParameterSpec.builder("param", paramType).build())
        if (method.returnType.isNotBlank()){
            val rType = ClassName("", method.returnType)
            f.returns(rType)
            f.addCode("return %T(%S)", rType, "")
            ctxType = method.type.withReturn.asParameterType(entity, paramType, rType)
        }

        f.addParameter(ParameterSpec.builder("ctx", ctxType).build())
        entityBuilder.addFunction(f.build())
    }

    return ModelEntiy(entityBuilder, superEntiy, superCompanion.build())
}



