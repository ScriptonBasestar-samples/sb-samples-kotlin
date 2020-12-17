package me.archmagece

import mu.KotlinLogging

val logger = KotlinLogging.logger { }


object Constants {
    const val URI_HEALTH = "/health_club"

    // by_none: read(list_summary), create(one)
    const val URI_BOARD_BASE = "/board"

    // by_id: read(one_detail), update, delete
    const val URI_BOARD_ONE = "$URI_BOARD_BASE/{id}"

    // by_keyword: read(list_summary)
    const val URI_BOARD_SEARCH = "$URI_BOARD_BASE/search"

    // by_ids: read(batch_detail), update(batch), delete(batch)
    const val URI_BOARD_BATCH = "$URI_BOARD_BASE/batch"

    const val URI_COMMENT_BASE = "/comment"
    const val URI_COMMENT_ONE = "$URI_COMMENT_BASE/one"
    const val URI_COMMENT_BATCH = "$URI_COMMENT_BASE/batch"
}