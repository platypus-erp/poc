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

    val newfield = PlatypusPropertyFactory<E>()

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
    val id = newfield.long()

    protected val newMethod = PlatypusMethodsFactory<E>()

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