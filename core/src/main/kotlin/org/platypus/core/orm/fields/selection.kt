package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.ColumnType
import org.platypus.core.SelectionValueNotFound

/**
 * @author chmuchme
 * @since 0.1
 * on 20/09/17.
 */
typealias SelectionValues = MutableMap<String, SelectionValue>


interface SelectionType

object SelectionRegistry {
    private val registry = mutableMapOf<SelectionType, SelectionValues>()

    private fun selection(s: SelectionType, value: String, active: Boolean, label: String): SelectionValue {
        return registry.computeIfAbsent(s, { mutableMapOf() })
                .computeIfAbsent(value, { SelectionValue(value, active, label) })
    }

    fun SelectionType.choice(_value: String, label: String): SelectionValue {
        return this@SelectionRegistry.selection(this, _value, true, label)
    }

    fun getFromValue(k: KassiopiaSelectionColumnType<*>, _value: String): SelectionValue {
        return this@SelectionRegistry.registry[k.type]?.get(_value) ?: throw SelectionValueNotFound(k.type, _value)
    }

}

fun createNewSelection(s: SelectionType, value: String, label: String) {
}

fun disable(v: SelectionValue) {

}

data class SelectionValue(val value: String, val active: Boolean, val label: String)

class KassiopiaSelectionColumnType<out T:SelectionType>(
        private val tableHolderName: String,
        private val fieldName: String,
        val type:T,
        private val defaultValue:T?,
        private val required: Boolean,
        private val readonly: Boolean
): ColumnType(!required){
    override fun sqlType() = "VARCHAR(30)"

    override fun valueFromDB(value: Any): Any {
        val res = super.valueFromDB(value)
        return when(res){
            is SelectionValue -> res
            is String -> SelectionRegistry.getFromValue(this, res)
            else -> throw SelectionValueNotFound(type, res)
        }
    }

    override fun valueToString(value: Any?): String {
        val r = value ?: defaultValue
        return super.valueToString(r)
    }

    override fun notNullValueToDB(value: Any): Any {
        return when(value){
            is SelectionValue -> value.value
            is String -> value
            else -> throw SelectionValueNotFound(type, value)
        }
    }
}