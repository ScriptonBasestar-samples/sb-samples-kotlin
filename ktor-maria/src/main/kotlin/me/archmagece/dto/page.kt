package me.archmagece.dto

data class PageRequest(
    val pageNo: Long,
    val pageSize: Long,
)

data class SearchRequest(
    val pageNo: Long,
    val pageSize: Long,
    val keyword: String,
)

enum class SortDirection {
    ASC, DESC,
}

data class SortRequest(
    val key: String,
    val direction: SortDirection,
)

data class PageResponse(
    // 1~
    val pageNo: Long,
    // 1~n
    val pageSize: Long,
    val totalRows: Long,
    val totalPages: Long,
    val hasPrev: Boolean,
    val hasNext: Boolean,
) {
    companion object {
        fun fromParam(pageNo: Long, pageSize: Long, totalRows: Long): PageResponse {
            val totalPages = totalRows / pageSize
            return PageResponse(
                pageNo = pageNo,
                pageSize = pageSize,
                totalRows = totalRows,
                totalPages = totalPages,
                hasPrev = when {
                    pageNo <= 1 -> false
//                totalPages < pageNo -> true
                    else -> true
                },
                hasNext = when {
//                pageNo < 0 -> true
                    totalPages <= pageNo -> false
                    else -> true
                }
            )
        }
    }
}
