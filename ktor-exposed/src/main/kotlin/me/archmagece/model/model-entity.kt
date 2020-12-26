package me.archmagece.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class ArticleEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ArticleEntity>(ArticleTable)
    var galleryUid by ArticleTable.galleryUid
    var userUid by ArticleTable.userUid

    var userNickname by ArticleTable.userNickname

    var title by ArticleTable.title
    var content by ArticleTable.content
    var formatType by ArticleTable.formatType

    var createdAt by ArticleTable.createdAt
    var updatedAt by ArticleTable.updatedAt

    val comments by CommentEntity referrersOn CommentTable.article
}

class CommentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CommentEntity>(CommentTable)
    var galleryUid by CommentTable.galleryUid
    var userUid by CommentTable.userUid

    var userNickname by CommentTable.userNickname

    var article by ArticleEntity referencedOn CommentTable.article

    var parentUid by CommentEntity referencedOn CommentTable.parentUid

    var content by CommentTable.content
    var formatType by CommentTable.formatType

    var createdAt by CommentTable.createdAt
    var updatedAt by CommentTable.updatedAt
}
