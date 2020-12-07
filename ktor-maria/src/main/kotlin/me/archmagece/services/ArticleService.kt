package me.archmagece.services

import me.archmagece.ArticleModel
import me.archmagece.BoardStatusCode
import me.archmagece.BoardStatusException
import me.archmagece.dao.ArticleEntity
import me.archmagece.dtos.*
import mu.KotlinLogging
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ArticleService {

    private val logger = KotlinLogging.logger { }

    fun writeOne(requestDto: ArticleWriteRequest): LongIdResponseDto =
        transaction {
//            addLogger(StdOutSqlLogger)
            logger.trace { "writeOne requestDto: $requestDto" }
            val id = ArticleModel.insertAndGetId {
                it[title] = requestDto.title
                it[content] = requestDto.content
            }
            LongIdResponseDto(id.value)
        }

    fun readOne(id: Long): ArticleDetailResponse =
        transaction {
            logger.trace { "readOne id: $id" }
            val result = ArticleEntity.findById(id)?.load(ArticleEntity::comments) ?: run {
                throw BoardStatusException(BoardStatusCode.ARTICLE_NOT_FOUND)
            }
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

    fun modifyOne(id: Long, requestDto: ArticleModifyRequest) : LongIdResponseDto =
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

    fun removeOne(id: Long): Int =
        transaction {
            logger.trace { "removeOne id: $id" }

            val numberOfDeletedItems = ArticleModel.deleteWhere {
                ArticleModel.id eq id
            }
            numberOfDeletedItems
        }

    fun readList(keyword: String, pageNo: Long, pageSize: Long): Pair<List<ArticleSummaryResponse>, PageResponse> =
        transaction {
            logger.trace { "readList: $keyword, $pageNo, $pageSize" }

            val query = ArticleEntity.find {
                ArticleModel.content.like(keyword)
            }
            val items = query.map {
                ArticleSummaryResponse(
                    id = it.id.value,
                    title= it.title,
                    content = it.content,
                )
            }

            val totalRows = query.count()
            val pageResponse = PageResponse.fromParam(pageNo, pageSize, totalRows)
            Pair(items, pageResponse)
        }

//    fun readSearch() {
//        logger.trace { "readSearch" }
//    }

    fun writeBatch(writeRequestList: List<ArticleWriteRequest>): List<Long> =
        transaction {
            logger.trace { "writeBatch: count ${writeRequestList.size}" }
            val ids = mutableListOf<Long>()
            writeRequestList.forEach { request ->
//                ArticleEntity.new {
//                    title = it.title,
//                    content = it.content,
//                }
                val id = ArticleModel.insertAndGetId {
                    it[title] = request.title
                    it[content] = request.content
                }
                ids.add(id.value)
            }
            ids
        }

//    fun readBatch(ids: Long) {
//        logger.trace { "readBatch" }
//    }

//    fun modifyBatch() {
//        logger.trace { "modifyBatch" }
//    }

    fun removeBatch(ids: List<Long>): Int =
        transaction {
            logger.trace { "removeBatch" }
            val count = ArticleModel.deleteWhere { ArticleModel.id.inList(ids) }
            count
        }

}