package org.platypus.core.orm.methods

import org.platypus.core.orm.Method
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 04/10/17.
 */
open class StaticMethodResultNoReturn<ENTITY : PlatypusEntity, PARAM>(private val stackOnChange: StaticMethodDefNoReturn<ENTITY, PARAM>) {
    val errors: MutableSet<RuntimeException> = mutableSetOf()
    //    val original = lazy { entity.klass[entity.id] }
    val warnings: MutableSet<String> = mutableSetOf()
    val ctx=  Context(mutableMapOf())
    private var specialContext: Array<out Pair<String, Any>> = emptyArray()

    fun Super(param: PARAM) {
        val sup = stackOnChange.comp
        val cop = this.makeCopy(stackOnChange.previous)
        sup(param, cop)
        specialContext.forEach { cop.ctx.remove(it.first) }
    }

    fun withContext(vararg value: Pair<String, Any>): StaticMethodResultNoReturn<ENTITY, PARAM> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: StaticMethodDefNoReturn<ENTITY, PARAM>?): StaticMethodResultNoReturn<ENTITY, PARAM> {
        val r = StaticMethodResultNoReturn(stackOnChange ?: StaticMethodDefNoReturn({ _, _ -> }))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class StaticMethodDefNoReturn<ENTITY : PlatypusEntity, PARAM>(
        var comp: (param: PARAM, res: StaticMethodResultNoReturn<ENTITY, PARAM>) -> Unit,
        var previous: StaticMethodDefNoReturn<ENTITY, PARAM>? = null
) : Method {

    fun extend(comp: (param: PARAM, res: StaticMethodResultNoReturn<ENTITY, PARAM>) -> Unit) {
        this.previous = StaticMethodDefNoReturn(this.comp, this.previous)
        this.comp = comp
    }
}

open class StaticMethodResultWithReturn<ENTITY : PlatypusEntity, PARAM , RETURN>(private val stackOnChange: StaticMethodDefWithReturn<ENTITY, PARAM, RETURN>) {
    val errors: MutableSet<RuntimeException> = mutableSetOf()
    //    val original = lazy { entity.klass[entity.id] }
    val warnings: MutableSet<String> = mutableSetOf()
    val ctx=  Context(mutableMapOf())
    private var specialContext: Array<out Pair<String, Any>> = emptyArray()

    fun Super(param: PARAM): RETURN {
        val sup = stackOnChange.comp
        val cop = this.makeCopy(stackOnChange.previous)
        var res = sup(param, cop)
        specialContext.forEach { cop.ctx.remove(it.first) }
        return res
    }

    fun withContext(vararg value: Pair<String, Any>): StaticMethodResultWithReturn<ENTITY, PARAM, RETURN> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: StaticMethodDefWithReturn<ENTITY, PARAM, RETURN>?): StaticMethodResultWithReturn<ENTITY, PARAM, RETURN> {
        val r = StaticMethodResultWithReturn(stackOnChange ?: StaticMethodDefWithReturn({ _, _ -> null }))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class StaticMethodDefWithReturn<ENTITY : PlatypusEntity, PARAM, RETURN>(
        var comp: (param: PARAM, res: StaticMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN,
        var previous: StaticMethodDefWithReturn<ENTITY, PARAM, RETURN>? = null
) : Method {

    fun extend(comp: (param: PARAM, res: StaticMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN) {
        this.previous = StaticMethodDefWithReturn(this.comp, this.previous)
        this.comp = comp
    }
}


interface StaticMethodReturn
interface StaticMethodParams