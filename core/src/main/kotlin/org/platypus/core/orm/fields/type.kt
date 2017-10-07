package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.ColumnType
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author chmuchme
 * @since 0.1
 * on 01/09/17.
 */
internal val DEFAULT_DATE_STRING_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-dd").withLocale(Locale.ROOT)
internal val DEFAULT_DATE_TIME_STRING_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSSSSS").withLocale(Locale.ROOT)

abstract class PlatypusColumnType<T : Any>(override var nullable: Boolean = false) : ColumnType() {

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




