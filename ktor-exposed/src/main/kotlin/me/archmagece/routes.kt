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
import me.archmagece.handler.BoardHandler
import me.archmagece.handler.CommonHandler
import java.util.UUID

/**
 * Given values from gateway
 */
fun gwHeader(call: ApplicationCall): Pair<UUID, String> {
    val userUid = UUID.fromString(call.request.header("X-USER-UID"))
        ?: throw IllegalArgumentException("X-USER-UID must be provided")
    val userNickname = call.request.header("X-USER-NICKNAME")
        ?: throw IllegalArgumentException("X-USER-NICKNAME must be provided")
    return Pair(userUid, userNickname)
}

fun Route.board(commonService: CommonHandler, boardService: BoardHandler) {

    get(Constants.URI_HEALTH) {
        logger.debug { "API ping" }
        call.respond(hashMapOf("healthy" to commonService.ping()))
    }
    route(Constants.URI_BOARD) {
        route("/{galleryUid}") {
            post {
                // create
                val (userUid, userNickname) = gwHeader(call)
                val galleryUid: UUID = UUID.fromString(call.parameters["galleryUid"]) ?: throw BoardStatusException(
                    BoardStatusCode.PARAM_MUST_PROVIDED
                )
                // val requestDto = call.receive<ArticleWriteRequest>()
                val requestDto = call.receive(ArticleWriteRequest::class)

                val responseData = boardService.writeArticle(galleryUid, userUid, userNickname, requestDto)

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
                val (userUid, userNickname) = gwHeader(call)
                val galleryUid: UUID = UUID.fromString(call.parameters["galleryUid"]) ?: throw BoardStatusException(
                    BoardStatusCode.PARAM_MUST_PROVIDED
                )
                val requestDto = call.receive(SearchRequest::class)

                val (responseData, responsePaging) = boardService.listArticle(
                    galleryUid,
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
            route("/{articleUid}") {
                get {
                    val (userUid, userNickname) = gwHeader(call)
                    val galleryUid: UUID = UUID.fromString(call.parameters["galleryUid"]) ?: throw BoardStatusException(
                        BoardStatusCode.PARAM_MUST_PROVIDED
                    )
                    val articleUid: UUID = UUID.fromString(call.parameters["articleUid"]) ?: throw BoardStatusException(
                        BoardStatusCode.ARTICLE_NOT_FOUND
                    )

                    val responseData = boardService.readArticle(galleryUid, userUid, articleUid)

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
                    val (userUid, userNickname) = gwHeader(call)
                    val articleUid: UUID = UUID.fromString(call.parameters["articleUid"]) ?: throw BoardStatusException(
                        BoardStatusCode.ARTICLE_NOT_FOUND
                    )
                    val requestDto = call.receive(ArticleModifyRequest::class)

                    val responseData = boardService.modifyArticle(articleUid, requestDto)

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
                    val (userUid, userNickname) = gwHeader(call)
                    val articleUid: UUID = UUID.fromString(call.parameters["articleUid"]) ?: throw BoardStatusException(
                        BoardStatusCode.ARTICLE_NOT_FOUND
                    )

                    val responseData = boardService.removeArticleBatch(listOf(articleUid))

                    call.respond(
                        HttpStatusCode.OK,
                        OneResponseWrapper(
                            code = BoardStatusCode.SUCCESS.code,
                            message = BoardStatusCode.SUCCESS.message,
                            data = responseData,
                        )
                    )
                }

                route("/{commentUid}") {
                    get {
                        val (userUid, userNickname) = gwHeader(call)
                        val articleUid: UUID = UUID.fromString(call.parameters["articleUid"])
                            ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)
                        val commentUid: UUID = UUID.fromString(call.parameters["commentUid"])
                            ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)

                        val (responseData, responsePaging) = boardService.listComment(articleUid)

                        call.respond(
                            HttpStatusCode.OK,
                            OneResponseWrapper(
                                code = BoardStatusCode.SUCCESS.code,
                                message = BoardStatusCode.SUCCESS.message,
                                data = responseData,
                            )
                        )
                    }
                    post {
                        val (userUid, userNickname) = gwHeader(call)
                        val articleUid: UUID = UUID.fromString(call.parameters["articleUid"])
                            ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)
                        val requestDto = call.receive(CommentWriteRequest::class)

                        val responseData = boardService.writeComment(articleUid, requestDto.content)

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
                            val (userUid, userNickname) = gwHeader(call)
                            val articleUid: UUID = UUID.fromString(call.parameters["articleUid"])
                                ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)
                            val commentUid: UUID = UUID.fromString(call.parameters["commentUid"])
                                ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)

                            val responseData = boardService.readComment(articleUid, commentUid)

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
                            val (userUid, userNickname) = gwHeader(call)
                            val articleUid: UUID = UUID.fromString(call.parameters["articleUid"])
                                ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)
                            val commentUid: UUID = UUID.fromString(call.parameters["commentUid"])
                                ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)
                            val requestDto = call.receive(CommentModifyRequest::class)

                            val responseData = boardService.modifyComment(articleUid, commentUid, requestDto.content)

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
                            val (userUid, userNickname) = gwHeader(call)
                            val articleUid: UUID = UUID.fromString(call.parameters["articleUid"])
                                ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)
                            val commentUid: UUID = UUID.fromString(call.parameters["commentUid"])
                                ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)
                            val requestDto = call.receive(CommentModifyRequest::class)

                            val responseData = boardService.removeCommentBatch(articleUid, listOf(commentUid))

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
    }
}
