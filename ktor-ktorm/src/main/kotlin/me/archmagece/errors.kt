package me.archmagece

enum class BoardStatusCode(val code: String, val message: String) {
    SUCCESS("A-00", "success"),

    // auth
    AUTH_TOKEN_EXPIRED("A-01", "토큰 발행 실패"),
    AUTH_TOKEN_ISSUE_FAIL("A-01", "토큰 발행 실패"),

    // request
    REQ_PATH_VARIABLE_NOT_FOUND("R-01", "path variable 필수"),

    // article
    PARAM_MUST_PROVIDED("G-01", "중복된 제목이 있습니다"),
    ARTICLE_DUPLICATE("G-02", "중복된 제목이 있습니다"),
    ARTICLE_NOT_FOUND("G-03", "값이 없습니다"),

    FAIL("E-01", "fail"),

    UNKNOWN("Z-99", "unknown error"),
}

class BoardStatusException(val statusCode: BoardStatusCode) : Exception(statusCode.message)
