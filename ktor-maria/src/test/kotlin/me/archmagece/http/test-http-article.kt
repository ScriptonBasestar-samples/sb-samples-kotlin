package me.archmagece.http

import me.archmagece.Constants
import io.ktor.http.*
import io.ktor.server.testing.*
import me.archmagece.dtos.ArticleModifyRequest
import me.archmagece.dtos.ArticleWriteRequest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TestHttpArticle: TestBase() {

    @Test
    fun `Article 생성 - success`() = boardServer {
        val roomId = "R_ABC"
        val userId = 1
        handleRequest {
            method = HttpMethod.Post
            uri = Constants.URI_BOARD_BASE
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader("X-ROOM-ID", roomId)
            addHeader("X-USER-ID", userId.toString())
            setBody(
                gson.toJson(
                    ArticleWriteRequest(
                        title = "title1",
                        content = "content1"
                    )
                )
            )
        }.apply {
            assertEquals(HttpStatusCode.Created, response.status())
            assertNotNull(response.content)
        }
    }

    @Test
    fun `Article 읽기 - success`() = boardServer {

    }
}