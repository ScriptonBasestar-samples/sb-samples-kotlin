package me.archmagece.model

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import java.util.Properties
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestBoardTable {

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
    fun `insert and delete`() = transaction {
        val galleryUid = UUID.randomUUID()
        val userUid = UUID.randomUUID()

        ArticleTable.insertAndGetId {
            it[ArticleTable.galleryUid] = galleryUid
            it[ArticleTable.userUid] = userUid
            it[ArticleTable.userNickname] = "nickname1"

            it[ArticleTable.title] = "title1"
            it[ArticleTable.content] = "content1"
            it[ArticleTable.formatType] = "md"
        }.let { id ->
            ArticleTable.deleteWhere { ArticleTable.id.eq(id) }

            val resultCount = ArticleTable.select { ArticleTable.id.eq(id) }.count()
            assertTrue { resultCount == 0L }
        }
    }

    @Test
    fun `updated at 시간 변경 확인`() = transaction {
        val galleryUid = UUID.randomUUID()
        val userUid = UUID.randomUUID()

        ArticleTable.insertAndGetId {
            it[ArticleTable.galleryUid] = galleryUid
            it[ArticleTable.userUid] = userUid
            it[ArticleTable.userNickname] = "nickname1"

            it[ArticleTable.title] = "title1"
            it[ArticleTable.content] = "content1"
            it[ArticleTable.formatType] = "md"
        }.let { id ->
            val article1 = ArticleTable.select {
                ArticleTable.id.eq(id)
            }.last()
            val article1Id = article1[ArticleTable.id]
            val article1Content = article1[ArticleTable.content]
            val article1CreatedAt = article1[ArticleTable.createdAt]
            val article1UpdatedAt = article1[ArticleTable.updatedAt]

            ArticleTable.update({ ArticleTable.id.eq(id.value) }) {
                it[ArticleTable.content] = "contentfix"
                // FIXME 자동. 업데이트시
                it[ArticleTable.updatedAt] = DateTime.now()
            }

            val article2 = ArticleTable.select {
                ArticleTable.id.eq(id)
            }.last()

            val article2Id = article2[ArticleTable.id]
            val article2Content = article2[ArticleTable.content]
            val article2CreatedAt = article2[ArticleTable.createdAt]
            val article2UpdatedAt = article2[ArticleTable.updatedAt]

            assertTrue { article1Id == article2Id }
            assertTrue { article1Content != article2Content }
            assertTrue { article1CreatedAt == article2CreatedAt }
            assertTrue { article1UpdatedAt != article2UpdatedAt }
        }
    }
}
