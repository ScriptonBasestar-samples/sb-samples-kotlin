package me.archmagece.dto


data class OneResponseWrapper<T>(
    val code: String,
    val message: String,
    val data: T,
)

data class ListResponseWrapper<T>(
    val code: String,
    val message: String,
    val data: T,
    val page: PageResponse,
)