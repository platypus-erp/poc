package org.platypus.modules.data

import org.platypus.modules.parser.visitor.FieldType

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */
enum class SimplePropertyType {
    STRING, BOOLEAN, DATE, DATETIME, TIME, FLOAT, INTEGER, DECIMAL, TEXT, BINARY, MANY2ONE, MANY2MANY, ONE2MANY, SELECTION, NONE
}
enum class FieldTypeCompute {
    NEWFIELD,COMPUTE, COMPUTESTORE
}
enum class MethodType {
    ONE,MULTI, STATIC
}
data class ParseResult(var packageModel: String = "",
                       val models: MutableSet<Model> = mutableSetOf(),
                       val imports: MutableMap<String, String> = mutableMapOf(),
                       val errors: MutableSet<String> = mutableSetOf())


data class RootModel(val name: String, val properties: Set<ModelProperty>)
data class ModelProperty(val name: String, val readonly: Boolean, val compute: FieldTypeCompute, val type: SimplePropertyType, val target:Pair<String,String>? = null)
data class ModelMethod(val name: String, val type: MethodType, val param: String)
open class Model(val name: String, val fields: Set<ModelMethod> = mutableSetOf(), val simpleField: Set<ModelProperty> = mutableSetOf(), val root:Boolean = true)
