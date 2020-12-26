package me.archmagece.route

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import me.archmagece.model.ArticleTable
import me.archmagece.model.CommentTable
import me.archmagece.module
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

open class TestBase {

    protected val logger = KotlinLogging.logger { }
    protected val gson = Gson()

    protected fun boardServer(callback: TestApplicationEngine.() -> Unit) {
        System.setProperty("testing", "true")
        System.setProperty("dbType", "h2")
        withTestApplication(Application::module) {
            transaction {
                SchemaUtils.dropDatabase()
                SchemaUtils.create(ArticleTable, CommentTable)
            }
            callback()
        }
    }
}
