package org.platypus.core.orm

import org.platypus.core.orm.fields.PlatypusProperty
import org.platypus.core.orm.methods.*

/**
 * @author chmuchme
 * @since 0.1
 * on 27/08/17.
 */
interface Method


class PlatypusMethodsFactory<ENTITY : PlatypusEntity<ENTITY>> {

    fun <PARAM, RETURN> multitest(
            comp: (entity: Collection<ENTITY>, param: PARAM, env: MultiMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN)
            = MultiMethodDefWithReturn(comp)

    fun <PARAM, RETURN> multi(comp: (entity: Collection<ENTITY>, param: PARAM, env: MultiMethodResultWithReturn<ENTITY, PARAM, RETURN>) -> RETURN) = MultiMethodDefWithReturn(comp)
}

