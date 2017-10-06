package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.ColumnType
import org.platypus.core.ColumnWidget
import org.platypus.core.HtmlWidget
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

class PlatypusPropertyFactory<E : PlatypusEntity> {
    fun string(string: String? = null,
               help: String? = null,
               pattern: String? = null,
               minSize: Int = Int.MIN_VALUE,
               maxSize: Int = Int.MAX_VALUE,
               defaultValue: String? = null,
               prefix: String? = null,
               suffix: String? = null,
               required: Boolean? = null,
               allowBlank: Boolean? = null,
               readonly: Boolean? = null,
               translate: Boolean? = null,
               trim: TrimType = TrimType.NONE,
               ecripted: Boolean? = null
    ) = PlatypusStringProperty<E>(
            string = string ?: "",
            help = help ?: "",
            regexp = pattern?.toRegex(),
            minSize = if (minSize == Int.MAX_VALUE) 0 else maxSize,
            maxSize = if (maxSize == Int.MAX_VALUE) 65 else maxSize,
            defaultValue = defaultValue ?: "",
            prefix = prefix ?: "",
            suffix = suffix ?: "",
            required = required ?: false,
            allowBlank = allowBlank ?: true,
            readonly = readonly ?: true,
            trim = trim,
            ecripted = ecripted ?: false)

    fun date(string: String? = null,
             help: String? = null,
             min: LocalDate? = null,
             max: LocalDate? = null,
             defaultValue: LocalDate? = null,
             required: Boolean? = null,
             readonly: Boolean? = null) =
            PlatypusDateProperty<E>(
                    string = string ?: "",
                    min = min ?: LocalDate.MIN,
                    max = max ?: LocalDate.MAX,
                    defaultValue = defaultValue ?: LocalDate.MIN,
                    required = required ?: false,
                    readonly = readonly ?: false)

    fun datetime(string: String? = null,
                 help: String? = null,
                 min: LocalDateTime? = null,
                 max: LocalDateTime? = null,
                 defaultValue: LocalDateTime? = null,
                 required: Boolean? = null,
                 readonly: Boolean? = null
    ) = PlatypusDateTimeProperty<E>(
            string = string ?: "",
            help = help ?: "",
            min = min ?: LocalDateTime.MIN,
            max = max ?: LocalDateTime.MAX,
            defaultValue = defaultValue ?: LocalDateTime.MIN,
            required = required ?: false,
            readonly = readonly ?: false)

    fun text(string: String? = null,
             help: String? = null,
             length: Int? = null,
             required: Boolean? = null,
             widget: ColumnWidget? = null)
            = PlatypusTextProperty<E>(string ?: "",
            help = help ?: "",
            length = length ?: 65535,
            required = required ?: false,
            widget = widget ?: HtmlWidget())

    fun integer(string: String? = null,
                help: String? = null,
                max: Int? = null,
                min: Int? = null,
                required: Boolean? = null,
                widget: ColumnWidget? = null)
            = PlatypusIntegerProperty<E>(string ?: "",
            help = help ?: "",
            max = max ?: 65535,
            min = min ?: 65535,
            required = required ?: false,
            widget = widget ?: HtmlWidget())

    fun long(string: String? = null,
             help: String? = null,
             max: Long? = null,
             min: Long? = null,
             required: Boolean? = null,
             widget: ColumnWidget? = null)
            = PlatypusLongProperty<E>(string ?: "",
            help = help ?: "",
            max = max ?: 65535,
            min = min ?: 65535,
            required = required ?: false,
            widget = widget ?: HtmlWidget())

    fun decimal(string: String? = null,
                help: String? = null,
                max: Float? = null,
                min: Float? = null,
                required: Boolean? = null,
                widget: ColumnWidget? = null)
            = PlatypusDecimalProperty<E>(string ?: "",
            help = help ?: "",
            max = max ?: 65535F,
            min = min ?: 65535F,
            required = required ?: false,
            widget = widget ?: HtmlWidget())

    fun boolean(string: String? = null,
                help: String? = null,
                required: Boolean? = null,
                widget: ColumnWidget? = null)
            = PlatypusBooleanProperty<E>(string ?: "",
            help = help ?: "",
            required = required ?: false,
            widget = widget ?: HtmlWidget())

    fun <T : SelectionType> selection(columnName: String,
                                      selection: T,
                                      string: String = columnName,
                                      help: String = "",
                                      required: Boolean? = null,
                                      readonly: Boolean? = null,
                                      wiget: ColumnWidget? = null) {
    }

    fun one2many(string: String? = null,
                 help: String? = null) = KassiopiaO2MColumnTmp(this, "")

    fun many2many(string: String? = null,
                  help: String? = null) = KassiopiaM2MColumnTmp(this, "")

    fun many2one(string: String? = null,
                 help: String? = null,
                 required: Boolean? = null,
                 readonly: Boolean? = null) = KassiopiaM2OColumnTmp()
}


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
    infix fun of(target: PlatypusLongProperty<*>): O2MColumn = O2MColumn(this.model, this.name, target)
}

class O2MColumn(val model: AbstractPlatypusModel<*>, val name: String, val target: PlatypusLongProperty<*>)
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

    fun setter(comp: (entity: E, value: String, param: ComputeSetStringResult<E>) -> Unit) = ComputeSetStringMethodDef(comp)
}

class PlatypusDateProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        private val min: LocalDate,
        private val max: LocalDate,
        private val defaultValue: LocalDate,
        private val required: Boolean,
        private val readonly: Boolean
) : PlatypusProperty(string, help)

class PlatypusDateTimeProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        private val min: LocalDateTime,
        private val max: LocalDateTime,
        private val defaultValue: LocalDateTime,
        private val required: Boolean,
        private val readonly: Boolean
) : PlatypusProperty(string, help)

class PlatypusTimeProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        private val min: LocalTime,
        private val max: LocalTime,
        private val defaultValue: LocalTime,
        private val required: Boolean,
        private val readonly: Boolean
) : PlatypusProperty(string, help)


class PlatypusTextProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        length: Int,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)

class PlatypusIntegerProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        max: Int,
        min: Int,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)

class PlatypusLongProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        max: Long,
        min: Long,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)

class PlatypusDecimalProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        max: Float,
        min: Float,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)

class PlatypusBooleanProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        required: Boolean,
        widget: ColumnWidget
) : PlatypusProperty(string, help)
