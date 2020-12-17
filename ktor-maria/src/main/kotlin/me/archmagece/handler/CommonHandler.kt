package me.archmagece.handler

import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

class CommonHandler {

    private val logger = KotlinLogging.logger { }

    fun ping() = transaction {
        logger.trace { "board service.ping ping" }
        TransactionManager.current().exec("select 1;") {
            it.next(); it.getString(1)
        }.equals("1")
    }

}
