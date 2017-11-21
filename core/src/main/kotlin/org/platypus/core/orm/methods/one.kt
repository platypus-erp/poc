package org.platypus.core.orm.methods

import org.platypus.core.orm.Method
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 04/10/17.
 */
open class CTX<ENTITY : PlatypusEntity, PARAM, RETURN>(private val stackOnChange: OneMethodDefWithReturn<ENTITY, PARAM, RETURN>) {
    val errors: MutableSet<RuntimeException> = mutableSetOf()
    //    val original = lazy { entity.klass[entity.id] }
    val warnings: MutableSet<String> = mutableSetOf()
    val ctx=  Context(mutableMapOf())
    private var specialContext: Array<out Pair<String, Any>> = emptyArray()

    fun Super(entity: ENTITY, param: PARAM): RETURN {
        val sup = stackOnChange.comp
        val cop = this.makeCopy(stackOnChange.previous)
        var res = sup(entity, param, cop)
        specialContext.forEach { cop.ctx.remove(it.first) }
        return res
    }

    fun withContext(vararg value: Pair<String, Any>): CTX<ENTITY, PARAM, RETURN> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: OneMethodDefWithReturn<ENTITY, PARAM, RETURN>?): CTX<ENTITY, PARAM, RETURN> {
        val r = CTX(stackOnChange ?: OneMethodDefWithReturn({ _, _, _ -> null}))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class OneMethodDefWithReturn<ENTITY : PlatypusEntity, PARAM, RETURN>(
        var comp: (entity: ENTITY, param: PARAM, res: CTX<ENTITY, PARAM, RETURN>) -> RETURN,
        var previous: OneMethodDefWithReturn<ENTITY, PARAM, RETURN>? = null
) : Method {

    infix fun extend(comp: (entity: ENTITY, param: PARAM, res: CTX<ENTITY, PARAM, RETURN>) -> RETURN) {
        this.previous = OneMethodDefWithReturn(this.comp, this.previous)
        this.comp = comp
    }
}