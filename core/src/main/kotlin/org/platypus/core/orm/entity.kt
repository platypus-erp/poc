package org.platypus.core.orm

import com.beust.klaxon.JsonObject
import com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table
import org.platypus.core.KassiopiaErp
import org.platypus.core.KassiopiaStep
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass


/**
 * @author chmuchme
 * @since 0.1
 * on 16/07/17.
 */


abstract class PlatypusEntity(id: EntityID<Long>) : LongEntity(id)

abstract class PlatypusEntityClass<E : PlatypusEntity>(val model: Model<E>, table : PlatypusTable) : LongEntityClass<E>(table)

object TableRegistry {
    val tables = mutableMapOf<String, PlatypusEntityClass<*>>()

    fun addTable(model: PlatypusEntityClass<*>) {
        if (KassiopiaErp.currentStep == KassiopiaStep.BOOT) {
            tables[model.table.tableName] = model
        }
    }
}