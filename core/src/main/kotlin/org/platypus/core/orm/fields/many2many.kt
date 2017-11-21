package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.ReferenceOption
import org.platypus.core.orm.AbstractPlatypusModel
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class KassiopiaM2MColumnTmp<E : PlatypusEntity<E>>(private val model: AbstractPlatypusModel<E>) {
    infix fun <T: PlatypusEntity<T>> of(target: AbstractPlatypusModel<T>) = PlatypusM2MColumnProperty<E, T>(target)
}

data class PlatypusM2MColumnProperty<E : PlatypusEntity<E>, T : PlatypusEntity<T>>(val model: AbstractPlatypusModel<T>) : PlatypusRefKey<E>