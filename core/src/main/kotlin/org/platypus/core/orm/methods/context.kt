package org.platypus.core.orm.methods


class Context(m: MutableMap<String, Any>) : MutableMap<String, Any> by m {

    fun asString(key: String): String? {
        return this[key] as String?
    }

    fun asString(key: String, defaultValue: String): String {
        return asString(key) ?: defaultValue
    }

}