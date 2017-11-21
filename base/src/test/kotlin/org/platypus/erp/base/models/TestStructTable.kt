package org.platypus.erp.base.models;

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.junit.Test;
import java.time.LocalDate

fun main(args: Array<String>) {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

    transaction {
        logger.addLogger(StdOutSqlLogger)

        create (PartnerTable, PartnerTitleTable, PartnerCategorieTable)

        val alexis = PartnerEntity.new {
            name = "Alexis"
            displayName = "Alexis"
            date = LocalDate.now()
            title = PartnerTitleEntity.new {

            }

        }

        println(alexis)

    }
}
