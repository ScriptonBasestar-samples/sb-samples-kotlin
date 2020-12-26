package me.archmagece

import com.codahale.metrics.Slf4jReporter
import com.google.gson.JsonSerializer
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.metrics.dropwizard.DropwizardMetrics
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import me.archmagece.dto.OneResponseWrapper
import me.archmagece.handler.BoardHandler
import me.archmagece.handler.CommonHandler
import me.archmagece.model.ArticleTable
import me.archmagece.model.CommentTable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.joda.time.DateTime
import org.slf4j.event.Level
import java.lang.reflect.Modifier
import java.text.DateFormat
import java.util.Properties
import java.util.UUID
import java.util.concurrent.TimeUnit

fun initConfig() = ConfigFactory.defaultApplication() ?: throw NullPointerException("init error on server.kt")

fun initDB(baseConfig: Config) {
    ConfigFactory.load().withFallback(baseConfig).apply {
        val dbType = getString("dbType")
        val config = getConfig(dbType)
        val hikariConfig = HikariConfig(
            Properties().apply {
                config.entrySet().forEach { e -> setProperty(e.key, config.getString(e.key)) }
            }
        )
        val ds = HikariDataSource(hikariConfig)
        Database.connect(ds)
    }
}

fun dbMigrate(baseConfig: Config) {
    ConfigFactory.load().withFallback(baseConfig).apply {
        val flyway = Flyway()
        val dbType = getString("dbType")
        val config = getConfig(dbType)

        flyway.setDataSource(
            config.getString("jdbcUrl"),
            config.getString("username"),
            config.getString("password"),
        )
        flyway.setSchemas(ArticleTable.tableName, CommentTable.tableName)
        flyway.setLocations("db/migration/$dbType")
        flyway.migrate()
    }
    // transaction {
    //     SchemaUtils.create(ArticleTable, CommentTable)
    // }
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

    val config = initConfig()
    initDB(config)
    dbMigrate(config)

    val commonService = CommonHandler()
    val boardService = BoardHandler()

    install(Routing) {
        board(commonService, boardService)
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
    }
}

fun main() {
    System.setProperty("testing", "false")
//    System.setProperty("dbType", "maria")
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module).start(wait = true)
}
