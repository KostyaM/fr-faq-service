package faq.fastreport.ru

import faq.fastreport.ru.di.appModule
import faq.fastreport.ru.faq.data.YamlTreeDataSource
import faq.fastreport.ru.faq.routing.FaqRouting
import faq.fastreport.ru.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    val env = applicationEngineEnvironment {
        connector {
            port = 8080
            host = "0.0.0.0"
        }
        module { module() }
    }
    embeddedServer(Netty, env).start(wait = true)
}


fun Application.module() {
    install(Koin) { modules(appModule) }

    // Заполняем БД данными из yaml файла
    log.info("Starting server initialization from yaml file")
    val yamlTreeDataSource by inject<YamlTreeDataSource>()
    yamlTreeDataSource.initialize()

    configureSecurity()

    // Роутинг /faq
    val faqRouting by inject<FaqRouting>()
    faqRouting.configure(this)
    log.info("Configured and ready!")
}
