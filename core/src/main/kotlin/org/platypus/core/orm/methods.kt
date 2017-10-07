package org.platypus.core.orm

import com.sun.org.apache.bcel.internal.generic.RETURN
import org.platypus.core.orm.fields.PlatypusProperty
import org.platypus.core.orm.methods.*
import kotlin.reflect.KClass

/**
 * @author chmuchme
 * @since 0.1
 * on 27/08/17.
 */
interface Method


class PlatypusMethodsFactory<ENTITY : PlatypusEntity> {

    fun <PARAM : MultiMethodParams, RETURN : MultiMethodReturn>
            multi(returnType: KClass<RETURN>,
                  comp: (entity: Collection<ENTITY>, param: PARAM, res: MultiMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN?)
            = MultiMethodDefWithReturn(comp)

    fun <PARAM : MultiMethodParams>
            multi(comp: (entity: Collection<ENTITY>, param: PARAM, res: MultiMethodResultNoReturn<ENTITY, PARAM>) -> Unit)
            = MultiMethodDefNoReturn(comp)

    fun <PARAM : OneMethodParams, RETURN : OneMethodReturn>
            one(paramType: KClass<PARAM>,
                returnType: KClass<RETURN>,
                comp: (entity: ENTITY, param: PARAM, res: OneMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN?)
            = OneMethodDefWithReturn(comp)

    fun <PARAM : OneMethodParams>
            one(paramType: KClass<PARAM>, comp: (entity: ENTITY, param: PARAM, res: OneMethodResultNoReturn<ENTITY, PARAM>) -> Unit)
            = OneMethodDefNoReturn(comp)

    @JvmName("model")
    fun <PARAM : StaticMethodParams, RETURN : StaticMethodReturn>
            static(paramType: KClass<PARAM>,
                returnType: KClass<RETURN>,
                comp: (param: PARAM, res: StaticMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN?)
            = StaticMethodDefWithReturn(comp)

    @JvmName("model")
    fun <PARAM : StaticMethodParams>
            static(paramType: KClass<PARAM>,
                   comp: (param: PARAM, res: StaticMethodResultNoReturn<ENTITY, PARAM>) -> Unit)
            = StaticMethodDefNoReturn(comp)

    fun group(vararg props: PlatypusProperty) = OnChangeGroup<ENTITY>(props)
}

