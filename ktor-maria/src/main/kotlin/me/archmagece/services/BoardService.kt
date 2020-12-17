package me.archmagece.services

import me.archmagece.ArticleModel
import me.archmagece.BoardStatusCode
import me.archmagece.BoardStatusException
import me.archmagece.CommentModel
import me.archmagece.dao.ArticleEntity
import me.archmagece.dao.CommentEntity
import me.archmagece.dtos.*
import mu.KotlinLogging
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class BoardService {

    private val logger = KotlinLogging.logger { }

    fun writeArticle(requestDto: ArticleWriteRequest): LongIdResponseDto =
        transaction {
//            addLogger(StdOutSqlLogger)
            logger.trace { "writeOne requestDto: $requestDto" }
            val id = ArticleModel.insertAndGetId {
                it[title] = requestDto.title
                it[content] = requestDto.content
            }
            LongIdResponseDto(id.value)
        }

    fun readArticle(id: Long): ArticleDetailResponse =
        transaction {
            logger.trace { "readOne id: $id" }
            val result = ArticleEntity.findById(id)?.load(ArticleEntity::comments) ?: throw BoardStatusException(
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

    fun modifyArticle(id: Long, requestDto: ArticleModifyRequest): LongIdResponseDto =
        transaction {
            logger.trace { "modifyOne id: $id, requestDto: $requestDto" }

            val entity = ArticleEntity.findById(id) ?: throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)
            requestDto.title?.let {
                entity.title = it
            }
            requestDto.content?.let {
                entity.content = it
            }
            LongIdResponseDto(entity.id.value)
        }

    fun removeArticleBatch(ids: List<Long>): Int =
        transaction {
            logger.trace { "remove ids: $ids" }

            val numberOfDeletedItems = ArticleModel.deleteWhere {
                ArticleModel.id.inList(ids)
            }
            numberOfDeletedItems
        }

    fun listArticle(keyword: String, pageNo: Long, pageSize: Long): Pair<List<ArticleSummaryResponse>, PageResponse> =
        transaction {
            logger.trace { "readList: $keyword, $pageNo, $pageSize" }

            val query = ArticleEntity.find {
                ArticleModel.content.like(keyword)
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

    fun listComment(articleId: Long, pageNo: Long, pageSize: Long): Pair<List<CommentResponse>, PageResponse> =
        transaction {
            logger.trace { "listComment: articleId: $articleId" }
            val query = CommentModel.select {
                CommentModel.article eq articleId
            }
            val items = query.map {
                CommentResponse(
                    id = it[CommentModel.id].value,
                    content = it[CommentModel.content],
                )
            }

            val totalRows = query.count()
            val pageResponse = PageResponse.fromParam(pageNo, pageSize, totalRows)
            Pair(items, pageResponse)
        }

    fun writeComment(articleId: Long, content: String) = transaction {
        logger.trace { "writeComment: articleId: $articleId, content: $content" }
        val id = CommentModel.insertAndGetId {
            it[CommentModel.article] = articleId
            it[CommentModel.content] = content
        }
        id
    }


    fun readComment(articleId: Long, contentId: Long) = transaction {
        logger.trace { "readComment articleId: $articleId, contentId: $contentId" }
        val query = CommentEntity.find {
            CommentModel.article.eq(articleId)
            CommentModel.id.eq(contentId)
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

    fun modifyComment(articleId: Long, commentId: Long, content: String) = transaction {
        logger.trace { "modifyComment articleId: $articleId, commentId: $commentId, content: $content" }
        CommentModel.update({
            CommentModel.article eq articleId
            CommentModel.id eq commentId
        }) {
            it[CommentModel.content] = content
        }
    }

    fun removeCommentBatch(articleId: Long, commentIds: List<Long>) = transaction {
        logger.trace { "removeComment articleId: $articleId, commentIds: $commentIds" }
        val numberOfDeletedItems = CommentModel.deleteWhere {
            CommentModel.article eq articleId
            CommentModel.id.inList(commentIds)
        }
        numberOfDeletedItems
    }
}