package me.archmagece.route

import com.google.gson.Gson
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.server.testing.setBody
import me.archmagece.Constants
import me.archmagece.dto.ArticleWriteRequest
import me.archmagece.dto.OneResponseWrapper
import me.archmagece.dto.UUIDResponseDto
import me.archmagece.genericType
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TestRouteBoard : TestBase() {

    @Test
    fun `Article 쓰기, 읽기, 삭제 - success`() = boardServer {
        val galleryId = UUID.randomUUID().toString()
        val userId = UUID.randomUUID().toString()
        val userNickname = "nickname1"

        var articleId: UUID
        handleRequest {
            method = HttpMethod.Post
            uri = "${Constants.URI_BOARD}/${galleryId}"
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
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
        }.response.let { response ->
            assertEquals(HttpStatusCode.Created, response.status())
            assertNotNull(response.content)
            val contentTypeText = assertNotNull(response.headers[HttpHeaders.ContentType])
            assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), ContentType.parse(contentTypeText))

            // val responseType = object : TypeToken<OneResponseWrapper<UUIDResponseDto>>() {}.type
            val responseType = genericType<OneResponseWrapper<UUIDResponseDto>>()
            val json = Gson().fromJson<OneResponseWrapper<UUIDResponseDto>>(response.content, responseType)
            articleId = json.data.id
        }

        handleRequest {
            method = HttpMethod.Put
            uri = "${Constants.URI_BOARD}/${galleryId}/${articleId}"
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
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
            val contentTypeText = assertNotNull(response.headers[HttpHeaders.ContentType])
            assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), ContentType.parse(contentTypeText))

            // val responseType = object : TypeToken<OneResponseWrapper<UUIDResponseDto>>() {}.type
            val responseType = genericType<OneResponseWrapper<UUIDResponseDto>>()
            val json = Gson().fromJson<OneResponseWrapper<UUIDResponseDto>>(response.content, responseType)
            articleId = json.data.id
        }
    }
}
