package org.platypus.core.orm

import org.platypus.core.orm.fields.PlatypusProperty
import org.platypus.core.orm.methods.*

/**
 * @author chmuchme
 * @since 0.1
 * on 27/08/17.
 */
interface Method


class PlatypusMethodsFactory<ENTITY : PlatypusEntity> {

    fun <PARAM, RETURN> multi(comp: (entity: Collection<ENTITY>, param: PARAM, env: MultiMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN) = MultiMethodDefWithReturn(comp)

    fun <PARAM, RETURN> one(comp: (entity: ENTITY, param: PARAM, env: CTX<ENTITY, PARAM, RETURN>) -> RETURN) = OneMethodDefWithReturn(comp)

    @JvmName("model")
    fun <PARAM, RETURN> static(comp: (param: PARAM, env: StaticMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN) = StaticMethodDefWithReturn(comp)

    fun group(vararg props: PlatypusProperty) = OnChangeGroup<ENTITY>(props)
}

