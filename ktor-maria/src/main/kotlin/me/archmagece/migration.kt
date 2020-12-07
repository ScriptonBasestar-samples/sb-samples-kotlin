package me.archmagece

import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway

object DBMigration {
    fun migrate() {
        val flyway = Flyway()
        val dbType = ConfigFactory.load().getString("db_type")
        val config = ConfigFactory.load().getConfig(dbType)

        flyway.setDataSource(
            config.getString("jdbcUrl"),
            config.getString("username"),
            config.getString("password"),
        )
        flyway.setSchemas("t_article", "t_comment")
        flyway.setLocations("db/migration/$dbType")
        flyway.migrate()
    }
}