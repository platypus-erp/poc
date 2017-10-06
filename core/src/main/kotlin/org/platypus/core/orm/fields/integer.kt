package org.platypus.core.orm.fields

import org.platypus.core.ColumnWidget
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class PlatypusIntegerProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        max: Int,
        min: Int,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)