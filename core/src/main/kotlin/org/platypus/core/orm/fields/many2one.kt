package org.platypus.core.orm.fields

import org.platypus.core.orm.AbstractPlatypusModel

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class KassiopiaM2OColumnTmp {
    infix fun of(target: AbstractPlatypusModel<*>): PlatypusM2OColumnProperty = PlatypusM2OColumnProperty(target)
}

class M2MColumn(val model: AbstractPlatypusModel<*>, val name: String, val target: M2MColumn)

data class PlatypusM2OColumnProperty(
        val model: AbstractPlatypusModel<*>
)