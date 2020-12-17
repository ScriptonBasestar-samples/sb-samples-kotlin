package me.archmagece.dto

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class ArticleWriteRequest(
    @field:NotNull
    val title: String,
    @field:NotNull
    val content: String,
)

data class ArticleModifyRequest(
    @field:NotEmpty
    @field:Length(min = 2)
    var title: String?,
    @field:NotEmpty
    @field:Length(min = 2)
    var content: String?
)

data class ArticleDetailResponse(
    val id: Long,
    val title: String,
    val content: String,
    val comments: List<CommentResponse>,
)

data class ArticleSummaryResponse(
    val id: Long,
    val title: String,
    val content: String,
)

data class CommentWriteRequest(
    @field:Size(min = 0, max = 3)
    @field:NotNull
    val content: String,
)

data class CommentModifyRequest(
    @field:Size(min = 0, max = 3)
    @field:NotNull
    val content: String,
)

data class CommentResponse(
//    val articleId: Long,
    val id: Long,
    val content: String,
)

// data class InspectionRequest(
//    @field:Size(min = 0, max = 3)
//    @field:NotNull
//    val token: String,
// )
