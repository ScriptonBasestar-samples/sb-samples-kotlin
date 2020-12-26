package me.archmagece.check

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

fun main() {
    println(DateTime.now().zone)
    println(DateTime.now().millis)
    println(DateTime.now(DateTimeZone.UTC).zone)
    println(DateTime.now(DateTimeZone.UTC).millis)
}
