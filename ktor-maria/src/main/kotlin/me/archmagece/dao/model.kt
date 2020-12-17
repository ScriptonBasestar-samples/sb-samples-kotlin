package me.archmagece

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.datetime

object ArticleModel : LongIdTable(name = "t_article") {
    val code = varchar("gallery_id", 30)
    val userId = long("user_id")

    val title = varchar("title", 100)
    val content = varchar("content", 1000)

    val formatType = varchar("format_type", 10)

    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}

object CommentModel : LongIdTable(name = "t_comment") {

    val article = reference("article", ArticleModel, onDelete = ReferenceOption.CASCADE)
//    val articleId = long("article").references(ArticleModel.id, onDelete = ReferenceOption.CASCADE)

    val userId = long("user_id")

    val content = varchar("content", 140)

    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
