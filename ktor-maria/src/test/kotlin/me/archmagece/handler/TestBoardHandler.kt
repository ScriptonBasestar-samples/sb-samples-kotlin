package me.archmagece.handler

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.archmagece.dto.ArticleWriteRequest
import me.archmagece.model.ArticleTable
import me.archmagece.model.CommentTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import java.util.Properties
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull

class TestBoardHandler {

    private val boardHandler = BoardHandler()

    @BeforeTest
    fun before() {
        System.setProperty("testing", "true")
        System.setProperty("db_type", "h2")
        val dbType = ConfigFactory.load().getString("db_type")
        val config = ConfigFactory.load().getConfig(dbType)
        val properties = Properties()
        config.entrySet().forEach { e -> properties.setProperty(e.key, config.getString(e.key)) }
        val hikariConfig = HikariConfig(properties)
        val ds = HikariDataSource(hikariConfig)
        val db = Database.connect(ds).apply {
            useNestedTransactions = true
        }
        transaction {
            SchemaUtils.drop(ArticleTable, CommentTable)
            SchemaUtils.create(ArticleTable, CommentTable)
        }
    }

    @Test
    fun `board - write article - normal success case`() {
        val galleryUid = UUID.randomUUID()
        val userUid = UUID.randomUUID()

        val response = boardHandler.writeArticle(
            galleryUid, userUid, "nickname1",
            ArticleWriteRequest(
                title = "title1",
                content = "content1",
                formatType = "md",
            )
        )
        assertNotNull(response)
    }
}
