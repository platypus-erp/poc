package org.platypus.core.orm.fields

import org.platypus.core.SelectionValueNotFound
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.StringColumnType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author chmuchme
 * @since 0.1
 * on 01/09/17.
 */
private val DEFAULT_DATE_STRING_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-dd").withLocale(Locale.ROOT)
private val DEFAULT_DATE_TIME_STRING_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSSSSS").withLocale(Locale.ROOT)

abstract class KassiopiaColumnType<T : Any>(override var nullable: Boolean = false) : ColumnType() {

    fun validate(valueNull: Any?):Set<String>{
        val typedValue = asType(valueNull)
        val errorsNull = validateNullableValue(typedValue).toMutableSet()
        if (errorsNull.isEmpty() && typedValue != null){
            errorsNull.addAll(validateNonNullValue(typedValue))
        }
        return errorsNull
    }
    abstract fun asType(v :Any?):T?
    abstract fun validateNullableValue(valueNull: T?): Set<String>
    abstract fun validateNonNullValue(valueNull: T): Set<String>
}

enum class TrimType {
    START, END, BOTH, NONE
}

class KassiopiaStringColumnType(
        private val tableHolderName: String,
        private val fieldName: String,
        private val regexp: Regex?,
        private val minSize: Int,
        private val maxSize: Int,
        private val defaultValue: String,
        private val prefix: String,
        private val suffix: String,
        private val required: Boolean,
        private val allowBlank: Boolean,
        private val readonly: Boolean,
        private val trim: TrimType,
        private val ecripted: Boolean,
        private val exposedType: StringColumnType = StringColumnType(maxSize)
) : IColumnType by exposedType, KassiopiaColumnType<String>(!required) {

    override fun asType(v: Any?): String? = v as String?

    override fun validateNullableValue(valueNull: String?): Set<String> {
        val errors = mutableSetOf<String>()
        errors.addIf(required && valueNull == null) {
            "${tableHolderName}.${fieldName} : the choice is required but don't have default value"
        }
        return errors
    }

    override fun validateNonNullValue(valueNull: String): Set<String> {
        val errors = mutableSetOf<String>()
        val value = valueNull ?: defaultValue
        val valueTrim = when (trim) {
            TrimType.NONE -> value
            TrimType.START -> value.trimStart()
            TrimType.END -> value.trimEnd()
            TrimType.BOTH -> value.trim()
        }
        errors.addIf(valueTrim.length < minSize) {
            "${tableHolderName}.${fieldName} : the choice min size is $minSize " +
                    "and the default value with prefix and sufix is ${valueTrim.length}"
        }

        errors.addIf(valueTrim.length > maxSize) {
            "${tableHolderName}.${fieldName} : the choice max size is $maxSize " +
                    "and the default value with prefix and suffix is ${valueTrim.length}"
        }
        errors.addIf(valueTrim.isBlank() && !allowBlank) {
            "${tableHolderName}.${fieldName} : the choice is blank but the column don't allow blank value"
        }
        errors.addIf(regexp != null && regexp.matches(valueTrim)) {
            "${tableHolderName}.${fieldName} : $valueTrim doesn't match ${regexp!!.pattern}"
        }
        return errors
    }

    override fun notNullValueToDB(value: Any): Any {
        if (value is String) {
            validateNonNullValue(value)
        }
        //TODO ecript data, How to ?
        return exposedType.notNullValueToDB(prefix + value + suffix)
    }


    override fun valueToDB(value: Any?): Any? {
        val valueDefault = value ?: defaultValue
        return exposedType.valueToDB(valueDefault)
    }
}

private fun <E> MutableCollection<E>.addIf(b: Boolean, function: () -> E) {}

class KassiopiaDateColumnType(
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

class KassiopiaDateTimeColumnType(
        private val tableHolderName: String,
        private val fieldName: String,
        private val min: LocalDateTime,
        private val max: LocalDateTime,
        private val defaultValue: LocalDateTime,
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

    private fun assertValue(value: LocalDateTime) {
        assert(value < min) {
            "${tableHolderName}.${fieldName} : the minimum value is $min but the actual value is $value"
        }

        assert(value > max) {
            "${tableHolderName}.${fieldName} : the maximum value is $max but the actual value is $value"
        }
        assert(value == LocalDateTime.MIN && required) {
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
            assertValue(value)
            val millis = value.toEpochSecond(ZoneOffset.UTC)
            return java.sql.Timestamp(millis)
        }
        if (value is LocalDate) {
            assertValue(LocalDateTime.of(value, LocalTime.MIN))
            val millis = value.toEpochDay()
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

class KassiopiaSelectionColumnType<out T:SelectionType>(
        private val tableHolderName: String,
        private val fieldName: String,
        val type:T,
        private val defaultValue:T?,
        private val required: Boolean,
        private val readonly: Boolean
): ColumnType(!required){
    override fun sqlType() = "VARCHAR(30)"

    override fun valueFromDB(value: Any): Any {
        val res = super.valueFromDB(value)
        return when(res){
            is SelectionValue -> res
            is String -> SelectionRegistry.getFromValue(this, res)
            else -> throw SelectionValueNotFound(type, res)
        }
    }

    override fun valueToString(value: Any?): String {
        val r = value ?: defaultValue
        return super.valueToString(r)
    }

    override fun notNullValueToDB(value: Any): Any {
        return when(value){
            is SelectionValue -> value.value
            is String -> value
            else -> throw SelectionValueNotFound(type, value)
        }
    }
}