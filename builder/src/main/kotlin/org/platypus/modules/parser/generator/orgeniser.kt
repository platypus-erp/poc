package org.platypus.modules.parser.generator

import org.platypus.modules.data.M2MModel
import org.platypus.modules.data.Model
import org.platypus.modules.data.SimplePropertyType

/**
 * @author chmuchme
 * @since 0.1
 * on 17/09/17.
 */
class ModuleModelOrganiser(models: Set<Model>) {

    private val rootModels: MutableSet<Model> = models.filter { it.root }.toMutableSet()
    private val inheritModels: MutableSet<Model> = models.filter { !it.root }.toMutableSet()

    fun resolveM2M() {
        val m2mModel = mutableSetOf<Model>()
        for (m in rootModels) {
            for (f in m.simpleField) {
                if (f.type == SimplePropertyType.MANY2MANY) {
                    val targetReplace = f.target!!.first.replace("this", m.name)
                    val targetWithoutCol = targetReplace.split(".")[0]
                    val col = targetReplace.split(".")[1]
                    M2MRegistry.getElement(m.name, f.name, targetWithoutCol, col)
                }
            }
        }
    }

}
//object UsersGroupRel : Many2ManyModel() {
//    val user = ref("user", Users)
//    val group = ref("group", Groups)
//
//}

object M2MRegistry {
    private val map: MutableMap<String, M2MModel> = mutableMapOf()

    fun generateM2M(): String {
        var res = ""
        for (m in map.values) {
            res += generateSingleM2M(m)
        }
        return res
    }

    private fun generateSingleM2M(m2m: M2MModel): String {
        return """
object ${m2m.name} : Many2ManyModel() {
    val ${m2m.field1.name} = ref("${m2m.field1.name}", ${m2m.field1.target})
    val ${m2m.field2.name} = ref("${m2m.field2.name}", ${m2m.field2.target})
}"""
    }

    fun getElement(modelName: String, fieldName: String, targetModelName: String, targetFieldName: String): M2MModel {
        val key1 = createKey(modelName, fieldName, targetModelName, targetFieldName)
        val key2 = createKey(targetModelName, targetFieldName, modelName, fieldName)
        return map.getOrDefault(key1, map[key2]) ?: createElement(modelName, fieldName, targetModelName, targetFieldName)
    }

    private fun createElement(modelName: String, fieldName: String, targetModelName: String, targetFieldName: String): M2MModel {
        val key1 = createKey(modelName, fieldName, targetModelName, targetFieldName)
        val key2 = createKey(targetModelName, targetFieldName, modelName, fieldName)
        val m2m = M2MModel(modelName, fieldName, targetModelName, targetFieldName)
        if (key2 !in map){
            map.put(key1, m2m)
        }
        return m2m
    }

    private fun createKey(modelName: String, fieldName: String, targetModelName: String, targetFieldName: String): String {
        return "$modelName.$fieldName--$targetModelName.$targetFieldName"
    }

}