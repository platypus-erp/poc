package org.platypus.core.orm.methods

import org.platypus.core.orm.Method

/**
 * @author chmuchme
 * @since 0.1
 * on 04/10/17.
 */
open class ComputeSetStringResult<PR>(private val stackOnChange: ComputeSetStringMethodDef<PR>) {
    val errors: MutableSet<RuntimeException> = mutableSetOf()
    //    val original = lazy { entity.klass[entity.id] }
    val warnings: MutableSet<String> = mutableSetOf()
    val ctx: MutableMap<String, Any> = mutableMapOf()
    private var specialContext: Array<out Pair<String, Any>> = emptyArray()

    fun Super(entity: PR, value: String): Unit {
        val sup = stackOnChange.comp
        val cop = this.makeCopy(stackOnChange.previous)
        val res = sup(entity, value, cop)
        specialContext.forEach { cop.ctx.remove(it.first) }
    }

    fun withContext(vararg value: Pair<String, Any>): ComputeSetStringResult<PR> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: ComputeSetStringMethodDef<PR>?): ComputeSetStringResult<PR> {
        val r = ComputeSetStringResult(stackOnChange ?: ComputeSetStringMethodDef({ _, _, _ -> }))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class ComputeSetStringMethodDef<E>(
        var comp: (entity: E, value: String, param: ComputeSetStringResult<E>) -> Unit,
        var previous: ComputeSetStringMethodDef<E>? = null
) : Method {

    fun extend(comp: (entity: E, value: String, param: ComputeSetStringResult<E>) -> Unit) {
        this.previous = ComputeSetStringMethodDef(this.comp, this.previous)
        this.comp = comp
    }
}