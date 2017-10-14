package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.ReferenceOption
import org.platypus.core.orm.AbstractPlatypusModel

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class PlatypusM2MColumn(private val model: AbstractPlatypusModel<*>, private val refOpt: ReferenceOption? = null)