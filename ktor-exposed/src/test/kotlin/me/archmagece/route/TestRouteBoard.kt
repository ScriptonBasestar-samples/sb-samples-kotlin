package me.archmagece.route

import io.ktor.http.*
import io.ktor.server.testing.setBody
import me.archmagece.Constants
import me.archmagece.dto.ArticleWriteRequest
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TestRouteBoard : TestBase() {

    @Test
    fun `Article 생성 - success`() = boardServer {
        val roomId = "R_ABC"
        val userId = UUID.randomUUID().toString()
        val userNickname = "nickname1"
        handleRequest {
            method = HttpMethod.Post
            uri = "${Constants.URI_BOARD}/${UUID.randomUUID()}"
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader("X-USER-UID", userId)
            addHeader("X-USER-NICKNAME", userNickname)
            setBody(
                gson.toJson(
                    ArticleWriteRequest(
                        title = "title1",
                        content = "content1",
                        formatType = "md",
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
