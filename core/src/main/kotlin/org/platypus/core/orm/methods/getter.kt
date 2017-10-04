package org.platypus.core.orm.methods

import org.platypus.core.orm.Method

/**
 * @author chmuchme
 * @since 0.1
 * on 02/10/17.
 */
open class ComputeGetStringResult<PR>(private val stackOnChange: ComputeGetStringMethodDef<PR>) {
    val errors: MutableSet<RuntimeException> = mutableSetOf()
    //    val original = lazy { entity.klass[entity.id] }
    val warnings: MutableSet<String> = mutableSetOf()
    val ctx: MutableMap<String, Any> = mutableMapOf()
    private var specialContext: Array<out Pair<String, Any>> = emptyArray()

    fun Super(entity: PR): String {
        val sup = stackOnChange.comp
        val cop = this.makeCopy(stackOnChange.previous)
        val res = sup(entity, cop)
        specialContext.forEach { cop.ctx.remove(it.first) }
        return res
    }

    fun withContext(vararg value: Pair<String, Any>): ComputeGetStringResult<PR> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: ComputeGetStringMethodDef<PR>?): ComputeGetStringResult<PR> {
        val r = ComputeGetStringResult(stackOnChange ?: ComputeGetStringMethodDef({ _, _ -> "" }))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class ComputeGetStringMethodDef<E>(
        var comp: (entity: E, param: ComputeGetStringResult<E>) -> String,
        var previous: ComputeGetStringMethodDef<E>? = null
) : Method {

    fun extend(comp: (entity: E, param: ComputeGetStringResult<E>) -> String) {
        this.previous = ComputeGetStringMethodDef(this.comp, this.previous)
        this.comp = comp
    }
}