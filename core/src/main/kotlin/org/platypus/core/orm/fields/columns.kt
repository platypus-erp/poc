package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.ColumnType
import org.platypus.core.ColumnWidget
import org.platypus.core.orm.AbstractPlatypusModel
import org.platypus.core.orm.PlatypusEntity
import org.platypus.core.orm.methods.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author chmuchme
 * @since 0.1
 * on 27/08/17.
 */
interface KassiopiaSimpleColumn<T : Any, CT : ColumnType> {
    fun freezeConfig(columnName: String)
}

class KassiopiaM2OColumnTmp {
    infix fun of(target: AbstractPlatypusModel<*>): PlatypusM2OColumnProperty = PlatypusM2OColumnProperty(target)
}

class KassiopiaM2MColumnTmp(private val model: AbstractPlatypusModel<*>, private val name: String) {
    infix fun of(target: M2MColumn): M2MColumn = M2MColumn(this.model, this.name, target)
}

class KassiopiaO2MColumnTmp(private val model: AbstractPlatypusModel<*>, private val name: String) {
    infix fun of(target: PlatypusLongProperty): O2MColumn = O2MColumn(this.model, this.name, target)
}

class O2MColumn(val model: AbstractPlatypusModel<*>, val name: String, val target: PlatypusLongProperty)
class M2MColumn(val model: AbstractPlatypusModel<*>, val name: String, val target: M2MColumn)

data class PlatypusM2OColumnProperty(
        val model: AbstractPlatypusModel<*>
)

sealed class PlatypusProperty(var string: String, var help: String)
class PlatypusStringProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        regexp: Regex?,
        minSize: Int,
        maxSize: Int,
        defaultValue: String,
        prefix: String,
        suffix: String,
        required: Boolean,
        allowBlank: Boolean,
        readonly: Boolean,
        trim: TrimType,
        ecripted: Boolean
) : PlatypusProperty(string, help) {

    fun onChange(comp: (entity: E, param: OnChangeResult<E>) -> Unit) = OnChangeMethodDef(comp)

    fun getter(comp: (entity: E, param: ComputeGetStringResult<E>) -> String) = ComputeGetStringMethodDef(comp)

    fun setter(comp: (entity: E, value: String, param: OnChangeResult<E>) -> Unit) {

    }
}

class PlatypusDateProperty(
        string: String,
        help: String,
        private val min: LocalDate,
        private val max: LocalDate,
        private val defaultValue: LocalDate,
        private val required: Boolean,
        private val readonly: Boolean
) : PlatypusProperty(string, help)

class PlatypusDateTimeProperty(
        string: String,
        help: String,
        private val min: LocalDateTime,
        private val max: LocalDateTime,
        private val defaultValue: LocalDateTime,
        private val required: Boolean,
        private val readonly: Boolean
) : PlatypusProperty(string, help)

class PlatypusTimeProperty(
        string: String,
        help: String,
        private val min: LocalTime,
        private val max: LocalTime,
        private val defaultValue: LocalTime,
        private val required: Boolean,
        private val readonly: Boolean
) : PlatypusProperty(string, help)


class PlatypusTextProperty(
        string: String,
        help: String,
        length: Int,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)

class PlatypusIntegerProperty(
        string: String,
        help: String,
        max: Int,
        min: Int,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)

class PlatypusLongProperty(
        string: String,
        help: String,
        max: Long,
        min: Long,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)

class PlatypusDecimalProperty(
        string: String,
        help: String,
        max: Float,
        min: Float,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)

class PlatypusBooleanProperty(
        string: String,
        help: String,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)
