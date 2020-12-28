package me.archmagece.model

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar


object ArticleTable : Table<Nothing>("t_article") {
    val id = uuid("id").primaryKey()

    val galleryUid = uuid("gallery_uid")
    val userUid = uuid("user_uid")

    val userNickname = varchar("user_nickname")

    val title = varchar("title")
    val content = varchar("content")
    val formatType = varchar("format_type")

    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}

object CommentTable : Table<Nothing>("t_comment") {
    val id = uuid("id").primaryKey()

    val galleryUid = uuid("gallery_uid")
    val userUid = uuid("user_uid")

    val userNickname = varchar("user_nickname")

    // val article = reference("article", ArticleTable, onDelete = ReferenceOption.CASCADE)
    //    val articleId = uuid("article").references(ArticleModel.id, onDelete = ReferenceOption.CASCADE)
    // val parentUid = uuid("parent_id").references(CommentTable.id)

    val content = varchar("content")
    val formatType = varchar("format_type")

    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
