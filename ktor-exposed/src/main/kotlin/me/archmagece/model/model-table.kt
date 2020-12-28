package me.archmagece.model

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

// val nowExpression = object : Expression<DateTime>() {
//     override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
//         append("now()")
//     }
// }

object ArticleTable : UUIDTable(name = "t_article") {
    val galleryUid = uuid("gallery_uid")
    val userUid = uuid("user_uid")

    val userNickname = varchar("user_nickname", 100)

    val title = varchar("title", 100)
    val content = varchar("content", 1000)
    val formatType = varchar("format_type", 10)

    // FIXME db now - on create
    // val createdAt = datetime("created_at").defaultExpression(nowExpression)
    val createdAt = datetime("created_at").default(DateTime.now())
    val updatedAt = datetime("updated_at").default(DateTime.now())

    val uniq = uniqueIndex("idx_article_uniq", galleryUid, userUid, id)
}

object CommentTable : UUIDTable(name = "t_comment") {
    val galleryUid = uuid("gallery_uid")
    val userUid = uuid("user_uid")

    val userNickname = varchar("user_nickname", 100)

    val article = reference("article", ArticleTable, onDelete = ReferenceOption.CASCADE)
    //    val articleId = uuid("article").references(ArticleModel.id, onDelete = ReferenceOption.CASCADE)

    val parentUid = uuid("parent_id").references(CommentTable.id)


    val content = varchar("content", 140)
    val formatType = varchar("format_type", 10)

    val createdAt = datetime("created_at").default(DateTime.now())
    val updatedAt = datetime("updated_at").default(DateTime.now())

    val uniq = uniqueIndex("idx_comment_uniq", galleryUid, userUid, article, id)
}
