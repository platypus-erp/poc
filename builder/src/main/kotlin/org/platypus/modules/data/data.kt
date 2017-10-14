package org.platypus.modules.data

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */
enum class SimplePropertyType {
    STRING, BOOLEAN, DATE, DATETIME, TIME, FLOAT, INTEGER, DECIMAL, TEXT, BINARY, MANY2ONE, MANY2MANY, ONE2MANY, SELECTION, NONE
}

enum class FieldTypeCompute {
    NEWFIELD, COMPUTE, COMPUTESTORE
}

enum class MethodType {
    ONE, MULTI, STATIC, NONE
}

data class ParseResult(var packageModel: String = "",
                       val models: MutableSet<Model> = mutableSetOf(),
                       val imports: MutableMap<String, String> = mutableMapOf(),
                       val errors: MutableSet<String> = mutableSetOf())

data class ParseResultObject(val model: Model, val errors: MutableSet<String>)


data class RootModel(val name: String,
                     val properties: Set<ModelProperty>)

data class ModelProperty(val name: String,
                         val readonly: Boolean,
                         val compute: FieldTypeCompute,
                         val type: SimplePropertyType,
                         val target: Pair<String, String>? = null)

data class ModelMethod(val name: String,
                       val type: MethodType,
                       val paramType: String,
                       val returnType: String)

open class Model(
        val pkg: String,
        val name: String,
        val method: Set<ModelMethod> = mutableSetOf(),
        val simpleField: Set<ModelProperty> = mutableSetOf(),
        val root: Boolean = true)

class M2MModel(modelName: String,
               fieldName: String,
               targetModelName: String,
               targetFieldName: String) :
        Model(modelName.firstLetterUpper() + fieldName.firstLetterUpper() + targetModelName.firstLetterUpper() + "${targetFieldName.firstLetterUpper()}Rel",
                emptySet(),
                setOf(
                        ModelProperty(fieldName, false, FieldTypeCompute.NEWFIELD, SimplePropertyType.MANY2ONE, Pair(targetModelName, "")),
                        ModelProperty(targetFieldName, false, FieldTypeCompute.NEWFIELD, SimplePropertyType.MANY2ONE, Pair(modelName, ""))
                )
        ) {
    val field1 = super.simpleField.first()
    val field2 = super.simpleField.last()
}
