package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.ColumnType
import org.platypus.core.ColumnWidget
import org.platypus.core.orm.PlatypusEntity
import java.time.LocalDate

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class PlatypusBooleanProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        val required: Boolean,
        val widget: ColumnWidget
) : PlatypusProperty(string, help)

class PlatypusBooleanColumnType(
        val tableHolderName: String,
        val fieldName: String,
        val required: Boolean
) : ColumnType(!required) {
    override fun sqlType()= "BOOLEAN"


}