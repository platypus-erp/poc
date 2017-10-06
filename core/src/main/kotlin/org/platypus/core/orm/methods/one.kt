package org.platypus.core.orm.methods

import org.platypus.core.orm.Method
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 04/10/17.
 */
open class OneMethodResultNoReturn<ENTITY : PlatypusEntity, PARAM : OneMethodParams>(private val stackOnChange: OneMethodDefNoReturn<ENTITY, PARAM>) {
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

    fun withContext(vararg value: Pair<String, Any>): OneMethodResultNoReturn<ENTITY, PARAM> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: OneMethodDefNoReturn<ENTITY, PARAM>?): OneMethodResultNoReturn<ENTITY, PARAM> {
        val r = OneMethodResultNoReturn(stackOnChange ?: OneMethodDefNoReturn({ _, _, _ -> }))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class OneMethodDefNoReturn<ENTITY : PlatypusEntity, PARAM : OneMethodParams>(
        var comp: (entity: ENTITY, param: PARAM, res: OneMethodResultNoReturn<ENTITY, PARAM>) -> Unit,
        var previous: OneMethodDefNoReturn<ENTITY, PARAM>? = null
) : Method {

    fun extend(comp: (entity: ENTITY, param: PARAM, res: OneMethodResultNoReturn<ENTITY, PARAM>) -> Unit) {
        this.previous = OneMethodDefNoReturn(this.comp, this.previous)
        this.comp = comp
    }
}

open class OneMethodResultWithReturn<ENTITY : PlatypusEntity, PARAM : OneMethodParams, RETURN : OneMethodReturn>(private val stackOnChange: OneMethodDefWithReturn<ENTITY, PARAM, RETURN>) {
    val errors: MutableSet<RuntimeException> = mutableSetOf()
    //    val original = lazy { entity.klass[entity.id] }
    val warnings: MutableSet<String> = mutableSetOf()
    val ctx: MutableMap<String, Any> = mutableMapOf()
    private var specialContext: Array<out Pair<String, Any>> = emptyArray()

    fun Super(entity: ENTITY, param: PARAM): RETURN? {
        val sup = stackOnChange.comp
        val cop = this.makeCopy(stackOnChange.previous)
        var res = sup(entity, param, cop)
        specialContext.forEach { cop.ctx.remove(it.first) }
        return res
    }

    fun withContext(vararg value: Pair<String, Any>): OneMethodResultWithReturn<ENTITY, PARAM, RETURN> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: OneMethodDefWithReturn<ENTITY, PARAM, RETURN>?): OneMethodResultWithReturn<ENTITY, PARAM, RETURN> {
        val r = OneMethodResultWithReturn(stackOnChange ?: OneMethodDefWithReturn({ _, _, _ -> null}))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class OneMethodDefWithReturn<ENTITY : PlatypusEntity, PARAM : OneMethodParams, RETURN : OneMethodReturn>(
        var comp: (entity: ENTITY, param: PARAM, res: OneMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN?,
        var previous: OneMethodDefWithReturn<ENTITY, PARAM, RETURN>? = null
) : Method {

    fun extend(comp: (entity: ENTITY, param: PARAM, res: OneMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN?) {
        this.previous = OneMethodDefWithReturn(this.comp, this.previous)
        this.comp = comp
    }
}


interface OneMethodReturn
interface OneMethodParams