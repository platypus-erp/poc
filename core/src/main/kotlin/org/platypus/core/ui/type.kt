package org.platypus.core.ui

import org.platypus.core.ViewTypeNotFound

/**
 * @author chmuchme
 * @since 0.1
 * on 07/09/17.
 */
object ViewTypeRegistry {
    private val views: MutableMap<String, ViewType> = mutableMapOf()

    operator fun set(name: String, typeDef: ViewType) {
        views.put(name, typeDef)
    }

    operator fun get(name: String): ViewType {
        return views[name] ?: throw ViewTypeNotFound(name)
    }

}

/**
 * Don't forget to add this type view inside the registry
 * with a ViewTypeRegistry["myView"] = this
 */
interface ViewType {
    val name: String
}