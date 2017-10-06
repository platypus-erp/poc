package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.ColumnType
import org.platypus.core.ColumnWidget
import org.platypus.core.HtmlWidget
import org.platypus.core.orm.AbstractPlatypusModel
import org.platypus.core.orm.Model
import org.platypus.core.orm.PlatypusEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author chmuchme
 * @since 0.1
 * on 27/08/17.
 */

class PlatypusPropertyFactory<E : PlatypusEntity>(private val model:Model<E>) {


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
                    help = help ?: "",
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
                 help: String? = null) = KassiopiaO2MColumnTmp(model, "")

    fun many2many(string: String? = null,
                  help: String? = null) = KassiopiaM2MColumnTmp(model, "")

    fun many2one(string: String? = null,
                 help: String? = null,
                 required: Boolean? = null,
                 readonly: Boolean? = null) = KassiopiaM2OColumnTmp()
}

open class PlatypusProperty(var string: String, var help: String)


















