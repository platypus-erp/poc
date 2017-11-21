package org.platypus.core.orm

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.SchemaUtils
import org.platypus.core.KassiopiaErp
import org.platypus.core.KassiopiaStep
import org.platypus.core.orm.methods.*
import kotlin.reflect.KClass


/**
 * @author chmuchme
 * @since 0.1
 * on 16/07/17.
 */


abstract class PlatypusEntity(id: EntityID<Long>) : LongEntity(id){

    fun checkRecusion(): Boolean {
        //TODO to implement copy on hexia ?
        return false
    }
}

abstract class PlatypusEntityClass<E : PlatypusEntity, OE: PlatypusEntityClass<E, OE>>
(val model: Model<E>,val platypusTable: PlatypusTable, val entityType: KClass<E>) : LongEntityClass<E>(platypusTable) {


}

object TableRegistry {
    val tables = mutableMapOf<String, PlatypusEntityClass<*,*>>()

    fun addTable(model: PlatypusEntityClass<*, *>) {
        if (KassiopiaErp.currentStep == KassiopiaStep.BOOT) {
            tables[model.table.tableName] = model
        }
    }
}