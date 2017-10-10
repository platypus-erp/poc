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
    NEWFIELD,COMPUTE,COMUPTESTORE
}
data class ParseResult(var packageModel: String = "",
                       val models: MutableSet<Model> = mutableSetOf(),
                       val imports: MutableMap<String, String> = mutableMapOf(),
                       val errors: MutableSet<String> = mutableSetOf())


data class RootModel(val name: String, val properties: Set<ModelSimpleProperty>)
data class ModelSimpleProperty(val name: String, val readonly: Boolean, val compute: FieldTypeCompute, val type: SimplePropertyType)
data class ModelField(val name: String, val type: String, val target: String, val ftype: FieldType = FieldType.FIELD)
open class Model(val name: String, val fields: Set<ModelField> = mutableSetOf(), val simpleField: Set<ModelSimpleProperty> = mutableSetOf(), val root:Boolean = true)
