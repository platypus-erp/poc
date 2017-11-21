package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.ColumnType
import org.platypus.core.orm.PlatypusEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
class PlatypusTimeProperty<E : PlatypusEntity<E>>(
        string: String,
        help: String,
        private val min: LocalTime,
        private val max: LocalTime,
        private val defaultValue: LocalTime,
        private val required: Boolean,
        private val readonly: Boolean
) : PlatypusProperty(string, help)

class PlatypusTimeColumnType(
        private val tableHolderName: String,
        private val fieldName: String,
        private val min: LocalTime,
        private val max: LocalTime,
        private val defaultValue: LocalTime,
        private val required: Boolean,
        private val readonly: Boolean
) : ColumnType(!required) {
    override fun sqlType(): String = "DATETIME"

    init {
        assert(required && defaultValue == null) {
            "${tableHolderName}.${fieldName} : the choice is required but don't have default Value"
        }
        assertValue(defaultValue)
    }

    private fun assertValue(value: LocalTime) {
        assert(value < min) {
            "${tableHolderName}.${fieldName} : the minimum value is $min but the actual value is $value"
        }

        assert(value > max) {
            "${tableHolderName}.${fieldName} : the maximum value is $max but the actual value is $value"
        }
        assert(value == LocalTime.MIN && required) {
            "${tableHolderName}.${fieldName} : the choice is null but the column don't allow blank value"
        }
    }

    override fun nonNullValueToString(value: Any): String {
        val dateTime = when (value) {
            is String -> LocalDateTime.parse(value)
            is LocalDateTime -> value
            is LocalDate -> LocalDateTime.of(value, LocalTime.MIN)
            is java.sql.Date -> LocalDateTime.of(value.toLocalDate(), LocalTime.MIN)
            is java.sql.Timestamp -> value.toLocalDateTime()
            else -> error("Unexpected value: $value")
        }
        return "'${dateTime.format(DEFAULT_DATE_TIME_STRING_FORMATTER)}'"
    }

    override fun notNullValueToDB(value: Any): Any {
        if (value is LocalDateTime) {
            assertValue(value.toLocalTime())
            val millis = value.toEpochSecond(ZoneOffset.UTC)
            return java.sql.Timestamp(millis)
        }
        error("Unexpected value: $value")
    }

    override fun valueToDB(value: Any?): Any? {
        val valueDefault = value ?: defaultValue
        return super.valueToDB(valueDefault)
    }

    override fun valueFromDB(value: Any): Any = when (value) {
        is LocalDateTime -> value
        is java.sql.Date -> LocalDateTime.of(value.toLocalDate(), LocalTime.MIN)
        is java.sql.Timestamp -> value.toLocalDateTime()
        is Int -> LocalDateTime.ofEpochSecond(value.toLong(), 0, ZoneOffset.UTC)
        is Long -> LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC)
        is String -> LocalDateTime.parse(value, DEFAULT_DATE_TIME_STRING_FORMATTER)
    // REVIEW
        else -> error("Unexpected value: $value")
    }
}