package org.platypus.core.orm.methods


class StringReturn(s:String):OneMethodReturn, StaticMethodReturn, MultiMethodReturn
class StringParam(s:String):OneMethodParams, StaticMethodParams, MultiMethodParams

class NoParam : OneMethodParams, StaticMethodParams, MultiMethodParams

class Context(m: MutableMap<String, Any>) : MutableMap<String, Any> by m {

    fun asString(key: String): String? {
        return this[key] as String?
    }

    fun asString(key: String, defaultValue: String): String {
        return asString(key) ?: defaultValue
    }

}