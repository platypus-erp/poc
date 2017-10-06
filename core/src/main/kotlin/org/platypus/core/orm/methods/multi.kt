package org.platypus.core.orm.methods

import org.platypus.core.orm.Method

/**
 * @author chmuchme
 * @since 0.1
 * on 04/10/17.
 */
open class MultiMethodResult<ENTITY, PARAM>(private val stackOnChange: MultiMethodDef<ENTITY, PARAM>) {
    val errors: MutableSet<RuntimeException> = mutableSetOf()
    //    val original = lazy { entity.klass[entity.id] }
    val warnings: MutableSet<String> = mutableSetOf()
    val ctx: MutableMap<String, Any> = mutableMapOf()
    private var specialContext: Array<out Pair<String, Any>> = emptyArray()

    fun Super(entity: ENTITY, param: PARAM): Unit {
        val sup = stackOnChange.comp
        val cop = this.makeCopy(stackOnChange.previous)
        sup(entity, param, cop)
        specialContext.forEach { cop.ctx.remove(it.first) }
    }

    fun withContext(vararg value: Pair<String, Any>): MultiMethodResult<ENTITY, PARAM> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: MultiMethodDef<ENTITY, PARAM>?): MultiMethodResult<ENTITY, PARAM> {
        val r = MultiMethodResult(stackOnChange ?: MultiMethodDef({ _, _, _ ->  }))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class MultiMethodDef<ENTITY, PARAM>(
        var comp: (entity: ENTITY, param: PARAM, res: MultiMethodResult<ENTITY, PARAM>) -> Unit,
        var previous: MultiMethodDef<ENTITY, PARAM>? = null
) : Method {

    fun extend(comp: (entity: ENTITY, param: PARAM, res: MultiMethodResult<ENTITY, PARAM>) -> Unit) {
        this.previous = MultiMethodDef(this.comp, this.previous)
        this.comp = comp
    }
}