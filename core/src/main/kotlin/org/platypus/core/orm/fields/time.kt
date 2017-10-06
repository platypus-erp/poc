package org.platypus.core.orm.fields

import org.platypus.core.orm.PlatypusEntity
import java.time.LocalTime

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class PlatypusTimeProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        private val min: LocalTime,
        private val max: LocalTime,
        private val defaultValue: LocalTime,
        private val required: Boolean,
        private val readonly: Boolean
) : PlatypusProperty(string, help)