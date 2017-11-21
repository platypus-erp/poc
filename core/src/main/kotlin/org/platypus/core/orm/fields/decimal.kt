package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.DecimalColumnType
import org.jetbrains.exposed.sql.IColumnType
import org.platypus.core.ColumnWidget
import org.platypus.core.orm.PlatypusEntity
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
interface DecimalPrecision {
    val scale: Int
    val precision: Int
    val roundingMode: RoundingMode

}


class PlatypusDecimalProperty<E : PlatypusEntity<E>>(
        string: String,
        help: String,
        max: Float,
        min: Float,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)

class PlatypusDecimalColumnType(
        private val tableHolderName: String,
        private val fieldName: String,
        private val min: Float,
        private val max: Float,
        private val precision: DecimalPrecision,
        private val defaultValue: BigDecimal,
        private val required: Boolean,
        private val readonly: Boolean,
        val exposedColumn: DecimalColumnType
) : IColumnType by exposedColumn, PlatypusColumnType<BigDecimal>(!required) {
    override fun asType(v: Any?): BigDecimal? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateNullableValue(valueNull: BigDecimal?): Set<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateNonNullValue(valueNull: BigDecimal): Set<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}