package org.platypus.core.orm.methods

import org.platypus.core.orm.Method
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 04/10/17.
 */
open class MultiMethodResultNoReturn<ENTITY : PlatypusEntity, PARAM : MultiMethodParams>(private val stackOnChange: MultiMethodDefNoReturn<ENTITY, PARAM>) {
    val errors: MutableSet<RuntimeException> = mutableSetOf()
    //    val original = lazy { entity.klass[entity.id] }
    val warnings: MutableSet<String> = mutableSetOf()
    val ctx: MutableMap<String, Any> = mutableMapOf()
    private var specialContext: Array<out Pair<String, Any>> = emptyArray()

    fun Super(entity: Collection<ENTITY>, param: PARAM): Unit {
        val sup = stackOnChange.comp
        val cop = this.makeCopy(stackOnChange.previous)
        sup(entity, param, cop)
        specialContext.forEach { cop.ctx.remove(it.first) }
    }

    fun withContext(vararg value: Pair<String, Any>): MultiMethodResultNoReturn<ENTITY, PARAM> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: MultiMethodDefNoReturn<ENTITY, PARAM>?): MultiMethodResultNoReturn<ENTITY, PARAM> {
        val r = MultiMethodResultNoReturn(stackOnChange ?: MultiMethodDefNoReturn({ _, _, _ -> }))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class MultiMethodDefNoReturn<ENTITY : PlatypusEntity, PARAM : MultiMethodParams>(
        var comp: (entity: Collection<ENTITY>, param: PARAM, res: MultiMethodResultNoReturn<ENTITY, PARAM>) -> Unit,
        var previous: MultiMethodDefNoReturn<ENTITY, PARAM>? = null
) : Method {

    fun extend(comp: (entity: Collection<ENTITY>, param: PARAM, res: MultiMethodResultNoReturn<ENTITY, PARAM>) -> Unit) {
        this.previous = MultiMethodDefNoReturn(this.comp, this.previous)
        this.comp = comp
    }
}

open class MultiMethodResultWithReturn<ENTITY : PlatypusEntity, PARAM : MultiMethodParams, RETURN : MultiMethodReturn>(private val stackOnChange: MultiMethodDefWithReturn<ENTITY, PARAM, RETURN>) {
    val errors: MutableSet<RuntimeException> = mutableSetOf()
    //    val original = lazy { entity.klass[entity.id] }
    val warnings: MutableSet<String> = mutableSetOf()
    val ctx: MutableMap<String, Any> = mutableMapOf()
    private var specialContext: Array<out Pair<String, Any>> = emptyArray()

    fun Super(entity: Collection<ENTITY>, param: PARAM): RETURN? {
        val sup = stackOnChange.comp
        val cop = this.makeCopy(stackOnChange.previous)
        var res = sup(entity, param, cop)
        specialContext.forEach { cop.ctx.remove(it.first) }
        return res
    }

    fun withContext(vararg value: Pair<String, Any>): MultiMethodResultWithReturn<ENTITY, PARAM, RETURN> {
        val newRes = this.makeCopy(stackOnChange)
        newRes.specialContext = value
        value.forEach { newRes.ctx.put(it.first, it.second) }
        return newRes
    }

    private fun makeCopy(stackOnChange: MultiMethodDefWithReturn<ENTITY, PARAM, RETURN>?): MultiMethodResultWithReturn<ENTITY, PARAM, RETURN> {
        val r = MultiMethodResultWithReturn(stackOnChange ?: MultiMethodDefWithReturn({ _, _, _ -> null}))
        r.errors.addAll(this.errors)
        r.warnings.addAll(this.warnings)
        r.ctx.putAll(this.ctx)
        r.specialContext = this.specialContext
        return r
    }
}


class MultiMethodDefWithReturn<ENTITY : PlatypusEntity, PARAM : MultiMethodParams, RETURN : MultiMethodReturn>(
        var comp: (entity: Collection<ENTITY>, param: PARAM, res: MultiMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN?,
        var previous: MultiMethodDefWithReturn<ENTITY, PARAM, RETURN>? = null
) : Method {

    fun extend(comp: (entity: Collection<ENTITY>, param: PARAM, res: MultiMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN?) {
        this.previous = MultiMethodDefWithReturn(this.comp, this.previous)
        this.comp = comp
    }
}


interface MultiMethodReturn
interface MultiMethodParams