package org.platypus.core.orm.methods

import org.platypus.core.orm.Method
import org.platypus.core.orm.PlatypusEntity
import org.platypus.core.orm.fields.PlatypusProperty

/**
 * @author chmuchme
 * @since 0.1
 * on 02/10/17.
 */
open class OnChangeResult<PR>(private val stackOnChange: OnChangeMethodDef<PR>) {
    val errors: MutableSet<RuntimeException> = mutableSetOf()
    //    val original = lazy { entity.klass[entity.id] }
    val warnings: MutableSet<String> = mutableSetOf()
    val ctx: MutableMap<String, Any> = mutableMapOf()
    private var specialContext: Array<out Pair<String, Any>> = emptyArray()

    fun Super(entity: PR): OnChangeResult<PR> {
        val sup = stackOnChange.comp
        val cop = this.makeCopy(stackOnChange.previous)
        sup(entity, cop)
        specialContext.forEach { cop.ctx.remove(it.first) }
        return cop
    }

    fun withContext(vararg value: Pair<String, Any>): OnChangeResult<PR> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: OnChangeMethodDef<PR>?): OnChangeResult<PR> {
        val r = OnChangeResult(stackOnChange ?: OnChangeMethodDef({ _, _ -> }))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class OnChangeMethodDef<E>(
        var comp: (entity: E, param: OnChangeResult<E>) -> Unit,
        var previous: OnChangeMethodDef<E>? = null
) : Method {

    fun extend(comp: (entity: E, param: OnChangeResult<E>) -> Unit) {
        this.previous = OnChangeMethodDef(this.comp, this.previous)
        this.comp = comp
    }
}

class OnChangeGroup<E : PlatypusEntity>(val props: Array<out PlatypusProperty>) {
    fun onChange(comp: (entity: E, param: OnChangeResult<E>) -> Unit) = OnChangeMethodDef(comp)
}