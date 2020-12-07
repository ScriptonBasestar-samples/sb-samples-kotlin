package me.archmagece

import io.ktor.application.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import me.archmagece.dtos.*
import me.archmagece.services.ArticleService
import me.archmagece.services.CommentService
import me.archmagece.services.CommonService
import mu.KotlinLogging

fun galleryHeader(call: ApplicationCall): Pair<String, Long> {
    val galleryId = call.request.header("X-GALLERY-ID")
        ?: throw IllegalArgumentException("X-GALLERY-ID must be provided")
    val userId = call.request.header("X-USER-ID")?.toLong()
        ?: throw IllegalArgumentException("X-USER-ID must be provided")
    return Pair(galleryId, userId)
}

fun galleryToken(call: ApplicationCall) =
    call.parameters["token"] ?: throw IllegalArgumentException("Path variable token not found")

fun Route.board(commonService: CommonService, articleService: ArticleService, commentService: CommentService) {
    val logger = KotlinLogging.logger { }

    get(Constants.URI_HEALTH) {
        logger.debug { "API ping" }
        call.respond(hashMapOf("healthy" to commonService.ping()))
    }
    route(Constants.URI_BOARD_BASE) {
        post {
            // create
            val (galleryId, userId) = galleryHeader(call)
            val requestDto = call.receive<ArticleWriteRequest>()

            call.respond(
                HttpStatusCode.Created, OneResponseWrapper(
                    code = BoardStatusCode.SUCCESS.code,
                    message = BoardStatusCode.SUCCESS.message,
                    data = articleService.writeOne(requestDto)
                )
            )
        }
        get {
            // list
            val (galleryId, userId) = galleryHeader(call)
            val pageRequest = call.receive(PageRequest::class)

            val (items, pageResponse) = articleService.readList("", pageRequest.pageNo, pageRequest.pageSize)

            call.respond(
                HttpStatusCode.OK, ListResponseWrapper(
                    code = BoardStatusCode.SUCCESS.code,
                    message = BoardStatusCode.SUCCESS.message,
                    data = items,
                    page = pageResponse,
                )
            )
        }
        route("/{id}") {
            get {
//                val uuid = UUID.fromString(call.parameters["uuid"]!!)
                val id: String = call.parameters["id"] ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)

            }
            put { }
            delete { }
        }
        get("/search") { }
        route("/batch") {
            get { }
            put { }
            delete { }
        }
    }
    route(Constants.URI_COMMENT_BASE) {
        get { }
        post { }
        route("/{id}") {
            get { }
            put { }
            delete { }
        }
        get("/search") { }
        route("/batch") {
            get { }
            put { }
            delete { }
        }
    }
}