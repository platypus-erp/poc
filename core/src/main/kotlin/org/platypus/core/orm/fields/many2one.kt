package org.platypus.core.orm.fields

import org.platypus.core.orm.AbstractPlatypusModel
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class KassiopiaM2OColumnTmp<E : PlatypusEntity>(private val model: AbstractPlatypusModel<E>) {
    infix fun <T: PlatypusEntity> of(target: AbstractPlatypusModel<T>) = PlatypusM2OColumnProperty<E, T>(target)
}

data class PlatypusM2OColumnProperty<E : PlatypusEntity, T : PlatypusEntity>(val model: AbstractPlatypusModel<T>) : PlatypusRefKey<E>