package org.platypus.core.orm


import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.platypus.core.orm.fields.*
import java.time.LocalDate

/**
 * @author chmuchme
 * @since 0.1
 * on 27/08/17.
 */
open class PlatypusTable(tableName: String) : LongIdTable(tableName) {

    protected fun many2oneColumn(name: String, prop: PlatypusM2OColumnProperty, tableRef: PlatypusTable) = reference(name, tableRef)

    protected fun stringColumn(name: String, prop: PlatypusStringProperty<*>): Column<String> {
        return this.registerColumn(name,
                PlatypusStringColumnType(
                        this.tableName, prop.string, prop.regexp, prop.minSize, prop.maxSize,
                        prop.defaultValue, prop.prefix, prop.suffix, prop.required,
                        prop.allowBlank, prop.readonly, prop.trim, prop.ecripted
                )
        )
    }

    protected fun dateColumn(name: String, prop: PlatypusDateProperty<*>): Column<LocalDate> {
        return this.registerColumn(name,
                PlatypusDateColumnType(this.tableName,
                        prop.string, prop.min, prop.max, prop.defaultValue,
                        prop.required, prop.readonly
                )
        )
    }

    protected fun one2manyColumn(name: String, propTarget: PlatypusLongProperty<*>, target: PlatypusTable): Column<EntityID<Long>> {
        return this.reference(name, target)
    }
}

sealed class AbstractPlatypusModel<E : PlatypusEntity> {

    protected val newfield = PlatypusPropertyFactory(this)
    fun compute(strProp: PlatypusStringProperty<E>) = ComputeStorePlatypusStringProperty(strProp)

}

open class Model<E : PlatypusEntity> : AbstractPlatypusModel<E>() {
    val id = newfield.long()
    protected val newMethod = PlatypusMethodsFactory<E>()

    protected fun computeStore(strProp: PlatypusStringProperty<E>) = ComputeStorePlatypusStringProperty(strProp)
}

open class InheritModel<E : PlatypusEntity> : AbstractPlatypusModel<E>() {
    protected val newMethod = PlatypusMethodsFactory<E>()

    protected fun computeStore(strProp: PlatypusStringProperty<E>) = ComputeStorePlatypusStringProperty(strProp)
}

open class Many2ManyModel(tableName: String = "") {
    val table = PlatypusTable(tableName)

    protected fun ref(name: String, target: PlatypusTable, refOpt: ReferenceOption? = null): Column<EntityID<Long>> =
            table.reference(name, target, refOpt)

}


open class TemporaryModel<E : PlatypusEntity>(tableName: String = "") : AbstractPlatypusModel<E>() {


}

open class View<E : PlatypusEntity>(tableName: String = "") : AbstractPlatypusModel<E>() {


}

open class MaterializedView<E : PlatypusEntity>(tableName: String = "") : AbstractPlatypusModel<E>() {


}