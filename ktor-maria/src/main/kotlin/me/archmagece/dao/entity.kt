package me.archmagece.dao

import me.archmagece.ArticleModel
import me.archmagece.CommentModel
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ArticleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ArticleEntity>(ArticleModel)

    var title by ArticleModel.title
    var content by ArticleModel.content

    var updatedAt by ArticleModel.updatedAt
    var createdAt by ArticleModel.createdAt

    val comments by CommentEntity referrersOn CommentModel.article
}

class CommentEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CommentEntity>(CommentModel)

    var article by ArticleEntity referencedOn CommentModel.article

    var content by CommentModel.content

    var updatedAt by ArticleModel.updatedAt
    var createdAt by ArticleModel.createdAt
}
