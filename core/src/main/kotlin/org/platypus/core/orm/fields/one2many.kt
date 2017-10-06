package org.platypus.core.orm.fields

import org.platypus.core.orm.AbstractPlatypusModel

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class KassiopiaO2MColumnTmp(private val model: AbstractPlatypusModel<*>, private val name: String) {
    infix fun of(target: PlatypusLongProperty<*>): O2MColumn = O2MColumn(this.model, this.name, target)
}

class O2MColumn(val model: AbstractPlatypusModel<*>, val name: String, val target: PlatypusLongProperty<*>)