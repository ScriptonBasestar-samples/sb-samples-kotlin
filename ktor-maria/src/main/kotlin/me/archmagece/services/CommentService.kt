package me.archmagece.services

import mu.KotlinLogging

class CommentService {

    private val logger = KotlinLogging.logger { }

    fun writeOne(articleId: Long, content: String) {
        logger.trace { "writeOne articleId: $articleId, content: $content" }
    }

//    fun readOne(id: Long) {
//        logger.trace { "readOne id: $id" }
//    }

    fun modifyOne(id: Long, title: String, content: String) {
        logger.trace { "modifyOne id: $id, title: $title, content: $content" }
    }

    fun removeOne(id: Long) {
        logger.trace { "removeOne id: $id" }
    }

    fun readList() {
        logger.trace { "readList" }
    }

//    fun readSearch() {
//        logger.trace { "readSearch" }
//    }

    fun writeBatch() {
        logger.trace { "writeBatch" }
    }

//    fun readBatch(ids: Long) {
//        logger.trace { "readBatch" }
//    }

//    fun modifyBatch() {
//        logger.trace { "modifyBatch" }
//    }

    fun removeBatch() {
        logger.trace { "removeBatch" }
    }

}