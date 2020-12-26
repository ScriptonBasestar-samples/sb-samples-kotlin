package me.archmagece

import com.google.common.reflect.TypeToken
import mu.KotlinLogging
import java.lang.reflect.Type

val logger = KotlinLogging.logger { }

object Constants {
    const val URI_HEALTH = "/health_club"
    const val URI_BOARD = "/board"
}

inline fun <reified T> genericType(): Type = object: TypeToken<T>() {}.type
