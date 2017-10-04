package org.platypus.core.orm.fields

import org.platypus.core.SelectionValueNotFound

/**
 * @author chmuchme
 * @since 0.1
 * on 20/09/17.
 */
typealias SelectionValues = MutableMap<String, SelectionValue>


open class SelectionType

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