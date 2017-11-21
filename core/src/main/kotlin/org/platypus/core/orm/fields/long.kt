package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.platypus.core.ColumnWidget
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
interface PlatypusRefKey<E : PlatypusEntity>

open class PlatypusLongProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        max: Long,
        min: Long,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusRefKey<E>, PlatypusProperty(string, help)

class PlatypusLongColumnType(
        private val tableHolderName: String,
        private val fieldName: String,
        private val min: Int,
        private val max: Int,
        private val defaultValue: Int,
        private val required: Boolean,
        private val readonly: Boolean,
        val exposedColumn: LongColumnType
) : IColumnType by exposedColumn, PlatypusColumnType<Long>(!required) {
    override fun asType(v: Any?): Long? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateNullableValue(valueNull: Long?): Set<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateNonNullValue(valueNull: Long): Set<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}