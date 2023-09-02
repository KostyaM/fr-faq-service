package faq.fastreport.ru

import faq.fastreport.ru.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val env = applicationEngineEnvironment {
        connector {
            port = 8080
            host = "127.0.0.1"
        }
        module { module() }
    }
    embeddedServer(Netty, env).start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureRouting()
}
