package me.archmagece.handler

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.archmagece.BoardStatusException
import me.archmagece.dto.ArticleModifyRequest
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
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class TestBoardHandler {

    private val boardHandler = BoardHandler()

    @BeforeTest
    fun before() {
        System.setProperty("testing", "true")
        System.setProperty("dbType", "h2")
        val dbType = ConfigFactory.load().getString("dbType")
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
    fun `board - article write - normal success case`() {
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

    @Test
    fun `board - write article, modify article, remove article, not exists article`() {
        val galleryUid = UUID.randomUUID()
        val userUid = UUID.randomUUID()

        val responseWrite = boardHandler.writeArticle(
            galleryUid, userUid, "nickname1",
            ArticleWriteRequest(
                title = "title1",
                content = "content1",
                formatType = "md",
            )
        )
        assertNotNull(responseWrite)
        println(responseWrite)
        val articleUid = responseWrite.id

        val responseModify = boardHandler.modifyArticle(
            articleUid,
            ArticleModifyRequest(
                title = "title1",
                content = "content1",
                formatType = "md",
            )
        )
        assertNotNull(responseModify)
        println(responseModify)

        val responseRead = boardHandler.readArticle(galleryUid, userUid, articleUid)
        assertNotNull(responseRead)

        val responseRemove = boardHandler.removeArticleBatch(listOf(responseModify.id))

        assertFailsWith<BoardStatusException> {
            val responseRead2 = boardHandler.readArticle(galleryUid, userUid, articleUid)
        }
    }

    @Test
    fun `board - write article, write comment, write comment`() {
        val galleryUid = UUID.randomUUID()
        val userUid = UUID.randomUUID()

        val responseArticleWrite = boardHandler.writeArticle(
            galleryUid, userUid, "nickname1",
            ArticleWriteRequest(
                title = "title1",
                content = "content1",
                formatType = "md",
            )
        )
        assertNotNull(responseArticleWrite)
        println(responseArticleWrite)
        val articleUid = responseArticleWrite.id

        val responseCommentWrite1 = boardHandler.writeComment(
            articleUid,
            "comment1"
        )
        assertNotNull(responseCommentWrite1)
        println(responseCommentWrite1)

        val responseCommentWrite2 = boardHandler.writeComment(
            articleUid,
            "comment2"
        )
        assertNotNull(responseCommentWrite2)
        println(responseCommentWrite2)

        assertFailsWith<BoardStatusException> {
            val responseRead2 = boardHandler.readArticle(galleryUid, userUid, articleUid)
        }
    }
}
