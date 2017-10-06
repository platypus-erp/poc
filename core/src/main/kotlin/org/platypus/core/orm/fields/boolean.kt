package org.platypus.core.orm.fields

import org.platypus.core.ColumnWidget
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class PlatypusBooleanProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)