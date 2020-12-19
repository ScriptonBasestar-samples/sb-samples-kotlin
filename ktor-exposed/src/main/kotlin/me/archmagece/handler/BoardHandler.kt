package me.archmagece.handler

import me.archmagece.BoardStatusCode
import me.archmagece.BoardStatusException
import me.archmagece.dto.ArticleDetailResponse
import me.archmagece.dto.ArticleModifyRequest
import me.archmagece.dto.ArticleSummaryResponse
import me.archmagece.dto.ArticleWriteRequest
import me.archmagece.dto.CommentResponse
import me.archmagece.dto.PageResponse
import me.archmagece.dto.UUIDResponseDto
import me.archmagece.model.ArticleEntity
import me.archmagece.model.ArticleTable
import me.archmagece.model.CommentEntity
import me.archmagece.model.CommentTable
import mu.KotlinLogging
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class BoardHandler {

    private val logger = KotlinLogging.logger { }

    fun writeArticle(
        galleryUid: UUID,
        userUid: UUID,
        userNickname: String,
        requestDto: ArticleWriteRequest
    ): UUIDResponseDto =
        transaction {
//            addLogger(StdOutSqlLogger)
            logger.trace { "writeOne requestDto: $requestDto" }
            val id = ArticleTable.insertAndGetId {
                it[ArticleTable.galleryUid] = galleryUid

                it[ArticleTable.userUid] = userUid
                it[ArticleTable.userNickname] = userNickname

                it[ArticleTable.title] = requestDto.title
                it[ArticleTable.content] = requestDto.content
                it[ArticleTable.formatType] = requestDto.formatType
            }
            UUIDResponseDto(id.value)
        }

    fun readArticle(galleryUid: UUID, userUid: UUID, articleUid: UUID): ArticleDetailResponse =
        transaction {
            logger.trace { "readOne - galleryUid: $galleryUid, userUid: $userUid, articleUid: $articleUid" }
            // TODO auth
            val result = ArticleEntity.find {
                ArticleTable.galleryUid.eq(galleryUid)
                ArticleTable.userUid.eq(userUid)
                ArticleTable.id.eq(articleUid)
            }.lastOrNull()?.load(ArticleEntity::comments) ?: throw BoardStatusException(
                BoardStatusCode.ARTICLE_NOT_FOUND
            )

            ArticleDetailResponse(
                id = result.id.value,
                title = result.title,
                content = result.content,
                comments = result.comments.map {
                    CommentResponse(
                        id = it.id.value,
                        content = it.content,
                    )
                },
            )
        }

    fun modifyArticle(articleUid: UUID, requestDto: ArticleModifyRequest): UUIDResponseDto =
        transaction {
            logger.trace { "modifyOne articleUid: $articleUid, requestDto: $requestDto" }

            val entity = ArticleEntity.findById(articleUid)
                ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)
            requestDto.title?.let {
                entity.title = it
            }
            requestDto.content?.let {
                entity.content = it
            }
            UUIDResponseDto(entity.id.value)
        }

    fun removeArticleBatch(uids: List<UUID>): Int =
        transaction {
            logger.trace { "remove ids: $uids" }

            val numberOfDeletedItems = ArticleTable.deleteWhere {
                ArticleTable.id.inList(uids)
            }
            numberOfDeletedItems
        }

    fun listArticle(
        galleryUid: UUID,
        keyword: String,
        pageNo: Long,
        pageSize: Long
    ): Pair<List<ArticleSummaryResponse>, PageResponse> =
        transaction {
            logger.trace { "readList: $keyword, $pageNo, $pageSize" }

            val query = ArticleEntity.find {
                ArticleTable.galleryUid.eq(galleryUid)
                ArticleTable.content.like(keyword)
            }
            val items = query.map {
                ArticleSummaryResponse(
                    id = it.id.value,
                    title = it.title,
                    content = it.content,
                )
            }

            val totalRows = query.count()
            val pageResponse = PageResponse.fromParam(pageNo, pageSize, totalRows)
            Pair(items, pageResponse)
        }

    fun listComment(articleUid: UUID): List<CommentResponse> =
        transaction {
            logger.trace { "listComment: articleUid: $articleUid" }
            val query = CommentTable.select {
                CommentTable.article eq articleUid
            }
            val items = query.map {
                CommentResponse(
                    id = it[CommentTable.id].value,
                    content = it[CommentTable.content],
                )
            }

            items
            // val totalRows = query.count()
            // val pageResponse = PageResponse.fromParam(pageNo, pageSize, totalRows)
            // Pair(items, pageResponse)
        }

    fun writeComment(articleUid: UUID, content: String) = transaction {
        logger.trace { "writeComment: articleUid: $articleUid, content: $content" }
        val id = CommentTable.insertAndGetId {
            it[CommentTable.article] = articleUid
            it[CommentTable.content] = content
        }
        id
    }

    fun readComment(articleUid: UUID, commentUid: UUID) = transaction {
        logger.trace { "readComment articleId: $articleUid, commentUid: $commentUid" }
        val query = CommentEntity.find {
            CommentTable.article.eq(articleUid)
            CommentTable.id.eq(articleUid)
        }

        Pair(
            query.map {
                CommentResponse(
                    id = it.id.value,
                    content = it.content
                )
            },
            query.count()
        )
    }

    fun modifyComment(articleUid: UUID, commentUid: UUID, content: String) = transaction {
        logger.trace { "modifyComment articleUid: $articleUid, commentUid: $commentUid, content: $content" }
        CommentTable.update({
            CommentTable.article eq articleUid
            CommentTable.id eq commentUid
        }) {
            it[CommentTable.content] = content
        }
    }

    fun removeCommentBatch(articleUid: UUID, commentIds: List<UUID>) = transaction {
        logger.trace { "removeComment articleUid: $articleUid, commentIds: $commentIds" }
        val numberOfDeletedItems = CommentTable.deleteWhere {
            CommentTable.article eq articleUid
            CommentTable.id.inList(commentIds)
        }
        numberOfDeletedItems
    }
}
