package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.DecimalColumnType
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.platypus.core.ColumnWidget
import org.platypus.core.orm.PlatypusEntity
import java.math.BigDecimal

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

class PlatypusIntegerColumnType(
        private val tableHolderName: String,
        private val fieldName: String,
        private val min: Int,
        private val max: Int,
        private val defaultValue: Int,
        private val required: Boolean,
        private val readonly: Boolean,
        val exposedColumn: IntegerColumnType
) : IColumnType by exposedColumn, PlatypusColumnType<Int>(!required) {
    override fun asType(v: Any?): Int? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateNullableValue(valueNull: Int?): Set<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateNonNullValue(valueNull: Int): Set<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}