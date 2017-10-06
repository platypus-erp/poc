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

    protected val newfield = PlatypusPropertyFactory<E>()

}

open class Model<E : PlatypusEntity> : AbstractPlatypusModel<E>() {
    val id = newfield.long()

    protected val newMethod = PlatypusMethodsFactory<E>()

    fun compute(strProp:PlatypusStringProperty<E>) = ComputeStorePlatypusStringProperty(strProp)
    fun computeStore(strProp:PlatypusStringProperty<E>) = ComputeStorePlatypusStringProperty(strProp)




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