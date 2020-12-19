package me.archmagece.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class ArticleEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ArticleEntity>(ArticleTable)
    val galleryUid by ArticleTable.galleryUid
    val userUid by ArticleTable.userUid

    val userNickname by ArticleTable.userNickname

    var title by ArticleTable.title
    var content by ArticleTable.content
    var formatType by ArticleTable.formatType

    var createdAt by ArticleTable.createdAt
    var updatedAt by ArticleTable.updatedAt

    val comments by CommentEntity referrersOn CommentTable.article
}

class CommentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CommentEntity>(CommentTable)
    val galleryUid by CommentTable.galleryUid
    val userUid by CommentTable.userUid

    val userNickname by CommentTable.userNickname

    var article by ArticleEntity referencedOn CommentTable.article

    var parentUid by CommentEntity referencedOn CommentTable.parentUid

    var content by CommentTable.content
    var formatType by CommentTable.formatType

    var createdAt by CommentTable.createdAt
    var updatedAt by CommentTable.updatedAt
}
