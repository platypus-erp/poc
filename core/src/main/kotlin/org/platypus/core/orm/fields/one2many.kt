package org.platypus.core.orm.fields

import org.platypus.core.orm.AbstractPlatypusModel
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class KassiopiaO2MColumnTmp<E : PlatypusEntity>(private val model: AbstractPlatypusModel<E>) {
    infix fun <T: PlatypusEntity> of(target: PlatypusLongProperty<T>)=  O2MColumn(this.model, target)
    infix fun <T: PlatypusEntity> of(target: PlatypusRefKey<T>) = O2MColumn(this.model, target)
}

class O2MColumn<E : PlatypusEntity, T: PlatypusEntity>(val model: AbstractPlatypusModel<E>, val target: PlatypusRefKey<T>)