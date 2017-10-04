package org.platypus.core.ui

import org.platypus.modules.base.Users
import org.platypus.core.orm.AbstractPlatypusModel
import org.platypus.core.orm.PlatypusEntity
import org.jetbrains.exposed.sql.Column

/**
 * @author chmuchme
 * @since 0.1
 * on 07/09/17.
 */
class ViewModelRegistry<E : PlatypusEntity>(val model: AbstractPlatypusModel<E>) {
    val views:MutableMap<ViewType, MutableMap<String, ViewNode>> = mutableMapOf()

    operator fun get(viewType: ViewType, id: String): ViewNode? {
        return views[viewType]?.get(id)
    }
}



class FormViewModelRegistry {
    private val forms = mutableMapOf<String, Form>()

    operator fun get(id: String): Form? {
        return forms[id]
    }
}


fun form(id: String, init: Form.() -> Unit): Form {
    val form = Form(id)
    form.init()
    return form
}

abstract class ViewNode(val name: String) {
    protected val children: MutableList<ViewNode> = mutableListOf()
    var groups: Array<String> = arrayOf()

    protected fun <T : ViewNode> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    val compiledView: String by lazy { this.render() }

    protected fun render(): String {
        return ""
    }
}

class Form(val id: String) : ViewNode("form"), ViewType {
    fun group(init: Group.() -> Unit) = initTag(Group(), init)
}

class Group : ViewNode("group") {
    var cssClass: Array<String> = arrayOf()
    var readonly: Boolean = false
    var invisible: Boolean = false

    fun group(init: Group.() -> Unit) = initTag(Group(), init)

    fun field(col: Column<*>, init: Field.() -> Unit) = initTag(Field(col), init)
}

class Field(col: Column<*>) : ViewNode("choice") {
    var label: String = col.name
}

val myForm = form("myForm") {
    group {
        cssClass = arrayOf("myCss")
        invisible = true
        group {
            field(Users.login) {
                label = "Login"
                groups = arrayOf("dskfn")
            }
        }

    }
}