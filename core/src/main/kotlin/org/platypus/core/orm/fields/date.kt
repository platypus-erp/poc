package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.ColumnType
import org.platypus.core.orm.PlatypusEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class PlatypusDateProperty<E : PlatypusEntity<E>>(
        string: String,
        help: String,
        var min: LocalDate,
        var max: LocalDate,
        var defaultValue: LocalDate,
        var required: Boolean,
        var readonly: Boolean
) : PlatypusProperty(string, help)

class PlatypusDateColumnType(
        private val tableHolderName: String,
        private val fieldName: String,
        private val min: LocalDate,
        private val max: LocalDate,
        private val defaultValue: LocalDate,
        private val required: Boolean,
        private val readonly: Boolean
) : ColumnType(!required) {

    init {
        assert(required && defaultValue == LocalDate.MIN) {
            "${tableHolderName}.${fieldName} : the choice is required but don't have default Value"
        }
        assertValue(defaultValue)
    }


    private fun assertValue(value: LocalDate) {
        assert(value < min) {
            "${tableHolderName}.${fieldName} : the minimum value is $min but the actual value is $value"
        }

        assert(value > max) {
            "${tableHolderName}.${fieldName} : the maximum value is $max but the actual value is $value"
        }
        assert(value == LocalDate.MIN && required) {
            "${tableHolderName}.${fieldName} : the choice is null but the column don't allow blank value"
        }
    }

    override fun notNullValueToDB(value: Any): Any {
        if (value is LocalDateTime) {
            assertValue(value.toLocalDate())
            val millis = value.toEpochSecond(ZoneOffset.UTC)
            return java.sql.Date(millis)
        }
        if (value is LocalDate) {
            assertValue(value)
            val millis = value.toEpochDay()
            return java.sql.Date(millis)
        }
        error("Unexpected value: $value")
    }

    override fun sqlType(): String = "DATE"

    override fun nonNullValueToString(value: Any): String {
        if (value is String) return value

        val dateTime = when (value) {
            is String -> LocalDate.parse(value)
            is LocalDateTime -> value.toLocalDate()
            is LocalDate -> value
            is java.sql.Date -> value.toLocalDate()
            is java.sql.Timestamp -> value.toLocalDateTime().toLocalDate()
            else -> error("Unexpected value: $value")
        }
        return "'${dateTime.format(DEFAULT_DATE_STRING_FORMATTER)}'"
    }

    override fun valueFromDB(value: Any): Any = when (value) {
        is LocalDate -> value
        is LocalDateTime -> value.toLocalDate()
        is java.sql.Date -> value.toLocalDate()
        is java.sql.Timestamp -> value.toLocalDateTime().toLocalDate()
        is Int -> LocalDate.ofEpochDay(value.toLong())
        is Long -> LocalDate.ofEpochDay(value)
        is String -> LocalDate.parse(value, DEFAULT_DATE_STRING_FORMATTER)
        else -> error("Unexpected value: $value")
    }

    override fun valueToDB(value: Any?): Any? {
        val valueDefault = value ?: defaultValue
        return super.valueToDB(valueDefault)
    }
}