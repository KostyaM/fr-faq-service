package faq.fastreport.ru.db

import io.ktor.server.config.*
import java.sql.Connection
import java.sql.DriverManager


class DatabaseFactory {

    fun init(config: ApplicationConfig) {
        val connection = connectToDb(config)
//        val statement = connection.createStatement()
    }

    private fun connectToDb(config: ApplicationConfig): Connection {
        Class.forName("org.postgresql.Driver")
        val url = config.property("postgres.jdbcURL").getString()
        val user = config.property("postgres.user").getString()
        val password = config.property("postgres.password").getString()

        return DriverManager.getConnection(url, user, password)
    }
}