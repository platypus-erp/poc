package org.platypus.core.orm


import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.*
import org.platypus.core.orm.fields.*
import org.platypus.core.orm.methods.*
import java.time.LocalDate

/**
 * @author chmuchme
 * @since 0.1
 * on 27/08/17.
 */
open class PlatypusTable(tableName: String) : LongIdTable(tableName) {

    val name: Column<String> = this.registerColumn("name", PlatypusStringColumnType(
            "Name", "Name", null, 0, 50,
            "", "", "", false,
            true, false, TrimType.NONE, false)
    )

    protected fun many2oneColumn(name: String, prop: PlatypusM2OColumnProperty<*, *>, tableRef: PlatypusTable) = reference(name, tableRef).nullable()

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

    protected fun booleanColumn(name: String, prop: PlatypusBooleanProperty<*>): Column<Boolean> {
        return this.registerColumn(name,
                PlatypusBooleanColumnType(this.tableName, prop.string, prop.required)
        )
    }

    protected fun integerColumn(name: String, prop: PlatypusIntegerProperty<*>): Column<Int> {
        return this.registerColumn(name,
                PlatypusBooleanColumnType(this.tableName, prop.string, true)
        )
    }

    protected fun longColumn(name: String, prop: PlatypusBooleanProperty<*>): Column<Long> {
        return this.registerColumn(name,
                PlatypusBooleanColumnType(this.tableName, prop.string, prop.required)
        )
    }

    protected fun one2manyColumn(name: String, propTarget: O2MColumn<*, *>, target: Column<EntityID<Long>>): Column<EntityID<Long>> {
        return this.reference(name, target)
    }

    protected fun optOne2manyColumn(name: String, propTarget: O2MColumn<*, *>, target: Column<EntityID<Long>?>): Column<EntityID<Long>?> {
        return this.reference(name, target)
    }
}

sealed class AbstractPlatypusModel<E : PlatypusEntity<E>> {

    protected val field = PlatypusPropertyFactory(this)
    fun compute(strProp: PlatypusStringProperty<E>) = ComputeStorePlatypusStringProperty(strProp)

}


data class SearchByNameParam(var value: String,
                             var operator: (col: ExpressionWithColumnType<String>, value: String) -> Op<Boolean>,
                             var additionnalCond: Op<Boolean>,
                             var limit: Int)

open class Model<E : PlatypusEntity<E>>(val compObj: PlatypusEntityClass<E>) : AbstractPlatypusModel<E>() {
    val id = field.long()
    val nameField = field.string("Name")

    protected val newMethod = PlatypusMethodsFactory<E>()

    protected fun computeStore(strProp: PlatypusStringProperty<E>) = ComputeStorePlatypusStringProperty(strProp)
}

open class Inherit<E : PlatypusEntity<E>> : AbstractPlatypusModel<E>() {
    val id = field.long()
    protected val newMethod = PlatypusMethodsFactory<E>()

    protected fun computeStore(strProp: PlatypusStringProperty<E>) = ComputeStorePlatypusStringProperty(strProp)
}

open class InheritModel<E : PlatypusEntity<E>> : AbstractPlatypusModel<E>() {
    protected val newMethod = PlatypusMethodsFactory<E>()

    protected fun computeStore(strProp: PlatypusStringProperty<E>) = ComputeStorePlatypusStringProperty(strProp)
}

open class Many2ManyTable(tableName: String = "") : PlatypusTable(tableName) {

    protected fun ref(name: String, target: PlatypusTable, refOpt: ReferenceOption? = null) = reference(name, target, refOpt)

}


open class TemporaryModel<E : PlatypusEntity<E>>(tableName: String = "") : AbstractPlatypusModel<E>() {


}

open class View<E : PlatypusEntity<E>>(tableName: String = "") : AbstractPlatypusModel<E>() {


}

open class MaterializedView<E : PlatypusEntity<E>>(tableName: String = "") : AbstractPlatypusModel<E>() {


}