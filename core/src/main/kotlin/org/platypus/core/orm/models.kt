package org.platypus.core.orm


import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.platypus.core.ColumnWidget
import org.platypus.core.HtmlWidget
import org.platypus.core.orm.fields.*
import org.platypus.core.orm.methods.OnChangeGroup
import org.platypus.core.orm.methods.OnChangeMethodDef
import org.platypus.core.orm.methods.OnChangeResult
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * @author chmuchme
 * @since 0.1
 * on 27/08/17.
 */
open class PlatypusTable(tableName: String) : LongIdTable(tableName) {

    protected fun many2one(name: String, prop: PlatypusM2OColumnProperty, tableRef: PlatypusTable) = reference(name, tableRef)
}

sealed class AbstractPlatypusModel<E : PlatypusEntity> {
    protected fun string(string: String? = null,
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

    protected fun date(string: String? = null,
                       help: String? = null,
                       min: LocalDate? = null,
                       max: LocalDate? = null,
                       defaultValue: LocalDate? = null,
                       required: Boolean? = null,
                       readonly: Boolean? = null) =
            PlatypusDateProperty(
                    string = string ?: "",
                    min = min ?: LocalDate.MIN,
                    max = max ?: LocalDate.MAX,
                    defaultValue = defaultValue ?: LocalDate.MIN,
                    required = required ?: false,
                    readonly = readonly ?: false)

    protected fun datetime(string: String? = null,
                           help: String? = null,
                           min: LocalDateTime? = null,
                           max: LocalDateTime? = null,
                           defaultValue: LocalDateTime? = null,
                           required: Boolean? = null,
                           readonly: Boolean? = null
    ) = PlatypusDateTimeProperty(
            string = string ?: "",
            help = help ?: "",
            min = min ?: LocalDateTime.MIN,
            max = max ?: LocalDateTime.MAX,
            defaultValue = defaultValue ?: LocalDateTime.MIN,
            required = required ?: false,
            readonly = readonly ?: false)

    protected fun text(string: String? = null,
                       help: String? = null,
                       length: Int? = null,
                       required: Boolean? = null,
                       widget: ColumnWidget? = null)
            = PlatypusTextProperty(string ?: "",
            help = help ?: "",
            length = length ?: 65535,
            required = required ?: false,
            widget = widget ?: HtmlWidget())

    protected fun integer(string: String? = null,
                          help: String? = null,
                          max: Int? = null,
                          min: Int? = null,
                          required: Boolean? = null,
                          widget: ColumnWidget? = null)
            = PlatypusIntegerProperty(string ?: "",
            help = help ?: "",
            max = max ?: 65535,
            min = min ?: 65535,
            required = required ?: false,
            widget = widget ?: HtmlWidget())

    protected fun long(string: String? = null,
                       help: String? = null,
                       max: Long? = null,
                       min: Long? = null,
                       required: Boolean? = null,
                       widget: ColumnWidget? = null)
            = PlatypusLongProperty(string ?: "",
            help = help ?: "",
            max = max ?: 65535,
            min = min ?: 65535,
            required = required ?: false,
            widget = widget ?: HtmlWidget())

    protected fun decimal(string: String? = null,
                          help: String? = null,
                          max: Float? = null,
                          min: Float? = null,
                          required: Boolean? = null,
                          widget: ColumnWidget? = null)
            = PlatypusDecimalProperty(string ?: "",
            help = help ?: "",
            max = max ?: 65535F,
            min = min ?: 65535F,
            required = required ?: false,
            widget = widget ?: HtmlWidget())

    protected fun boolean(string: String? = null,
                          help: String? = null,
                          required: Boolean? = null,
                          widget: ColumnWidget? = null)
            = PlatypusBooleanProperty(string ?: "",
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

    protected fun one2many(string: String? = null,
                           help: String? = null) = KassiopiaO2MColumnTmp(this, "")

    protected fun many2many(string: String? = null,
                            help: String? = null) = KassiopiaM2MColumnTmp(this, "")

    protected fun many2one(string: String? = null,
                           help: String? = null,
                           required: Boolean? = null,
                           readonly: Boolean? = null) = KassiopiaM2OColumnTmp()

    protected fun onChange(comp: (entity: E, param: OnChangeResult<E>) -> Unit) = OnChangeMethodDef(comp)

//    protected fun privateMutli(comp: (entity: E, Super: MutliMethodContext<E>) -> Unit) = MultiMethodDef(comp)
//    fun m2m(refName: String): M2MColumn {
//        return m2mByName[refName] ?: throw NotFoundException("Many2Many column $refName not found in de model ${this::class.simpleName}")
//    }
//    protected fun computeGet(vararg depends: Column<*>, comp: (entity: E, Super: ComputeMethod<E>) -> Unit) =
//            ComputeMethodDef(depends.toMutableSet(), ComputeType.GET, comp)
//
//    protected fun computeSet(vararg depends: Column<*>, comp: (entity: E, Super: ComputeMethod<E>) -> Unit) =
//            ComputeMethodDef(depends.toMutableSet(), ComputeType.SET, comp)

}

open class Model<E : PlatypusEntity> : AbstractPlatypusModel<E>() {
    val id = long()

    fun onChangeGroup(vararg props: PlatypusProperty) = OnChangeGroup<E>(props)
}

open class Many2ManyModel(tableName: String = "") {
    val table = PlatypusTable(tableName)

    protected fun ref(name: String, target: AbstractPlatypusModel<*>, refOpt: ReferenceOption? = null): Column<EntityID<Int>> =
            table.reference(name, target.table, refOpt)

}


open class TemporaryModel<E : PlatypusEntity>(tableName: String = "") : AbstractPlatypusModel<E>(tableName) {


}

open class View<E : PlatypusEntity>(tableName: String = "") : AbstractPlatypusModel<E>(tableName) {


}

open class MaterializedView<E : PlatypusEntity>(tableName: String = "") : AbstractPlatypusModel<E>(tableName) {


}