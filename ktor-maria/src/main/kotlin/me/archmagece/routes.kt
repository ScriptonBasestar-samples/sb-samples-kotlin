package me.archmagece

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import me.archmagece.dto.ArticleModifyRequest
import me.archmagece.dto.ArticleWriteRequest
import me.archmagece.dto.CommentModifyRequest
import me.archmagece.dto.CommentWriteRequest
import me.archmagece.dto.ListResponseWrapper
import me.archmagece.dto.OneResponseWrapper
import me.archmagece.dto.SearchRequest
import me.archmagece.handler.BoardService
import me.archmagece.handler.CommonService

/**
 * Given values from gateway
 */
fun gwHeader(call: ApplicationCall): Pair<Long, String> {
    val userId =
        call.request.header("X-USER-ID")?.toLong() ?: throw IllegalArgumentException("X-USER-ID must be provided")
    val userNickname =
        call.request.header("X-USER-NICKNAME") ?: throw IllegalArgumentException("X-USER-NICKNAME must be provided")
    return Pair(userId, userNickname)
}

fun Route.board(commonService: CommonService, boardService: BoardService) {

    get(Constants.URI_HEALTH) {
        logger.debug { "API ping" }
        call.respond(hashMapOf("healthy" to commonService.ping()))
    }
    route(Constants.URI_BOARD_BASE) {
        post {
            // create
            val (userId, userNickname) = gwHeader(call)
//            val requestDto = call.receive<ArticleWriteRequest>()
            val requestDto = call.receive(ArticleWriteRequest::class)

            val responseData = boardService.writeArticle(requestDto)

            call.respond(
                HttpStatusCode.Created,
                OneResponseWrapper(
                    code = BoardStatusCode.SUCCESS.code,
                    message = BoardStatusCode.SUCCESS.message,
                    data = responseData,
                )
            )
        }
        get {
            // list
            val (userId, userNickname) = gwHeader(call)
            val requestDto = call.receive(SearchRequest::class)

            val (responseData, responsePaging) = boardService.listArticle(
                requestDto.keyword,
                requestDto.pageNo,
                requestDto.pageSize
            )

            call.respond(
                HttpStatusCode.OK,
                ListResponseWrapper(
                    code = BoardStatusCode.SUCCESS.code,
                    message = BoardStatusCode.SUCCESS.message,
                    data = responseData,
                    page = responsePaging,
                )
            )
        }
        route("/{articleId}") {
            get {
                val (userId, userNickname) = gwHeader(call)
                val articleId: Long = call.parameters["articleId"]?.toLong() ?: throw BoardStatusException(
                    BoardStatusCode.ARTICLE_NOT_FOUND
                )

                val responseData = boardService.readArticle(articleId)

                call.respond(
                    HttpStatusCode.OK,
                    OneResponseWrapper(
                        code = BoardStatusCode.SUCCESS.code,
                        message = BoardStatusCode.SUCCESS.message,
                        data = responseData,
                    )
                )
            }
            put {
                val (userId, userNickname) = gwHeader(call)
                val articleId: Long = call.parameters["articleId"]?.toLong() ?: throw BoardStatusException(
                    BoardStatusCode.ARTICLE_NOT_FOUND
                )
                val requestDto = call.receive(ArticleModifyRequest::class)

                val responseData = boardService.modifyArticle(articleId, requestDto)

                call.respond(
                    HttpStatusCode.OK,
                    OneResponseWrapper(
                        code = BoardStatusCode.SUCCESS.code,
                        message = BoardStatusCode.SUCCESS.message,
                        data = responseData,
                    )
                )
            }
            delete {
                val (userId, userNickname) = gwHeader(call)
                val articleId: Long = call.parameters["articleId"]?.toLong() ?: throw BoardStatusException(
                    BoardStatusCode.ARTICLE_NOT_FOUND
                )

                val responseData = boardService.removeArticleBatch(listOf(articleId))

                call.respond(
                    HttpStatusCode.OK,
                    OneResponseWrapper(
                        code = BoardStatusCode.SUCCESS.code,
                        message = BoardStatusCode.SUCCESS.message,
                        data = responseData,
                    )
                )
            }

            route(Constants.URI_COMMENT_BASE) {
                get {
                    val (userId, userNickname) = gwHeader(call)
                    val articleId: Long = call.parameters["articleId"]?.toLong() ?: throw BoardStatusException(
                        BoardStatusCode.ARTICLE_NOT_FOUND
                    )

                    val (responseData, responsePaging) = boardService.listComment(articleId, 0, 0)

                    call.respond(
                        HttpStatusCode.OK,
                        ListResponseWrapper(
                            code = BoardStatusCode.SUCCESS.code,
                            message = BoardStatusCode.SUCCESS.message,
                            data = responseData,
                            page = responsePaging,
                        )
                    )
                }
                post {
                    val (userId, userNickname) = gwHeader(call)
                    val articleId: Long = call.parameters["articleId"]?.toLong() ?: throw BoardStatusException(
                        BoardStatusCode.ARTICLE_NOT_FOUND
                    )
                    val requestDto = call.receive(CommentWriteRequest::class)

                    val responseData = boardService.writeComment(articleId, requestDto.content)

                    call.respond(
                        HttpStatusCode.OK,
                        OneResponseWrapper(
                            code = BoardStatusCode.SUCCESS.code,
                            message = BoardStatusCode.SUCCESS.message,
                            data = responseData,
                        )
                    )
                }
                route("/{commentId}") {
                    get {
                        val (userId, userNickname) = gwHeader(call)
                        val articleId: Long = call.parameters["articleId"]?.toLong() ?: throw BoardStatusException(
                            BoardStatusCode.ARTICLE_NOT_FOUND
                        )
                        val commentId: Long = call.parameters["commentId"]?.toLong() ?: throw BoardStatusException(
                            BoardStatusCode.ARTICLE_NOT_FOUND
                        )

                        val responseData = boardService.readComment(articleId, commentId)

                        call.respond(
                            HttpStatusCode.OK,
                            OneResponseWrapper(
                                code = BoardStatusCode.SUCCESS.code,
                                message = BoardStatusCode.SUCCESS.message,
                                data = responseData,
                            )
                        )
                    }
                    put {
                        val (userId, userNickname) = gwHeader(call)
                        val articleId: Long = call.parameters["articleId"]?.toLong() ?: throw BoardStatusException(
                            BoardStatusCode.ARTICLE_NOT_FOUND
                        )
                        val commentId: Long = call.parameters["commentId"]?.toLong() ?: throw BoardStatusException(
                            BoardStatusCode.ARTICLE_NOT_FOUND
                        )
                        val requestDto = call.receive(CommentModifyRequest::class)

                        val responseData = boardService.modifyComment(articleId, commentId, requestDto.content)

                        call.respond(
                            HttpStatusCode.OK,
                            OneResponseWrapper(
                                code = BoardStatusCode.SUCCESS.code,
                                message = BoardStatusCode.SUCCESS.message,
                                data = responseData,
                            )
                        )
                    }
                    delete {
                        val (userId, userNickname) = gwHeader(call)
                        val articleId: Long = call.parameters["articleId"]?.toLong() ?: throw BoardStatusException(
                            BoardStatusCode.ARTICLE_NOT_FOUND
                        )
                        val commentId: Long = call.parameters["commentId"]?.toLong() ?: throw BoardStatusException(
                            BoardStatusCode.ARTICLE_NOT_FOUND
                        )
                        val requestDto = call.receive(CommentModifyRequest::class)

                        val responseData = boardService.removeCommentBatch(articleId, listOf(commentId))

                        call.respond(
                            HttpStatusCode.OK,
                            OneResponseWrapper(
                                code = BoardStatusCode.SUCCESS.code,
                                message = BoardStatusCode.SUCCESS.message,
                                data = responseData,
                            )
                        )
                    }
                }
            }
        }
    }

//    route(Constants.URI_COMMENT_BASE) {
//        get { }
//        post { }
//        route("/{id}") {
//            get { }
//            put { }
//            delete { }
//        }
//        get("/search") { }
//        route("/batch") {
//            get { }
//            put { }
//            delete { }
//        }
//    }
}
