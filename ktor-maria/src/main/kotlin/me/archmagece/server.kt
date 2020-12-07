package me.archmagece

import com.codahale.metrics.Slf4jReporter
import com.google.gson.JsonSerializer
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.metrics.dropwizard.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.archmagece.dtos.OneResponseWrapper
import me.archmagece.services.ArticleService
import me.archmagece.services.CommentService
import me.archmagece.services.CommonService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.slf4j.event.Level
import java.lang.reflect.Modifier
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun initConfig() = ConfigFactory.defaultApplication() ?: throw NullPointerException("init error on server.kt")

fun initDB(baseConfig: Config) {
    ConfigFactory.load().withFallback(baseConfig).apply {
        val dbType = getString("db_type")
        val config = getConfig(dbType)
        val hikariConfig = HikariConfig(Properties().apply {
            config.entrySet().forEach { e -> setProperty(e.key, config.getString(e.key)) }
        })
        val ds = HikariDataSource(hikariConfig)
        Database.connect(ds)
    }
}

fun dbMigrate() {
    // FIXME cli로 이동 할 필요
//    DBMigration.migrate()
    transaction {
        SchemaUtils.create(ArticleModel, CommentModel)
    }
}

val serializer: JsonSerializer<DateTime> = JsonSerializer { src, _, context ->
    context.serialize(src.millis)
}

fun Application.module() {
    install(Compression)
    install(DefaultHeaders)
    install(CallLogging) {
        filter { call -> !call.request.path().startsWith(Constants.URI_HEALTH) }
        level = Level.TRACE
        mdc("executionId") {
            UUID.randomUUID().toString()
        }
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
            excludeFieldsWithModifiers(Modifier.TRANSIENT)
            registerTypeAdapter(DateTime::class.java, serializer)
        }
    }
    install(DropwizardMetrics) {
        Slf4jReporter.forRegistry(registry)
            .outputTo(log)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build()
            .start(10, TimeUnit.SECONDS)
    }

    initDB(initConfig())
    dbMigrate()

    val commonService = CommonService()
    val articleService = ArticleService()
    val commentService = CommentService()

    install(Routing) {
        board(commonService, articleService, commentService)
    }
    install(StatusPages) {
        exception<IllegalArgumentException> {
            call.respond(
                HttpStatusCode.BadRequest,
                OneResponseWrapper(
                    code = BoardStatusCode.FAIL.code,
                    message = BoardStatusCode.FAIL.message,
                    data = ""
                )
            )
        }
        // 문법체크
        exception<BoardStatusException> { cause ->
            call.respond(
                HttpStatusCode.OK,
                OneResponseWrapper(
                    code = cause.statusCode.code,
                    message = cause.statusCode.message,
                    data = ""
                )
            )
        }
//        exception<BoardStatusException> { cause ->
//            call.respond(HttpStatusCode.OK) {
//                me.archmagece.ResponseWrapper(
//                    code = cause.statusCode.code,
//                    message = cause.statusCode.message,
//                    data = ""
//                )
//            }
//        }
    }
}


fun main() {
    System.setProperty("testing", "false")
//    System.setProperty("db_type", "maria")
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module).start(wait = true)
}