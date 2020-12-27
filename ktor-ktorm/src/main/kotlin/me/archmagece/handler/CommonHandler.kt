package me.archmagece.handler

import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.ktorm.database.Database

class CommonHandler(val database: Database) {

    private val logger = KotlinLogging.logger { }

    fun ping(): Boolean = database.useTransaction {
        logger.trace { "board service.ping ping" }
        TransactionManager.current().exec("select 1;") {
            it.next(); it.getString(1)
        }.equals("1")
    }
}
