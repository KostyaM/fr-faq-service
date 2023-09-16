package faq.fastreport.ru

import faq.fastreport.ru.di.appModule
import faq.fastreport.ru.faq.data.YamlTreeDataSource
import faq.fastreport.ru.faq.routing.FaqRouting
import faq.fastreport.ru.session.routing.SessionRouting
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) = EngineMain.main(args)


fun Application.module() {
    install(Koin) { modules(appModule(this@module)) }

    // Заполняем БД данными из yaml файла
    log.info("Starting server initialization from yaml file")
    val yamlTreeDataSource by inject<YamlTreeDataSource>()
    yamlTreeDataSource.initialize(
        environment.config.property("faqConfig.filePath").getString()
    )

    // Роутинг session
    val sessionRouting by inject<SessionRouting>()
    sessionRouting.configure()

    // Роутинг /faq
    val faqRouting by inject<FaqRouting>()
    faqRouting.configure()
    log.info("Configured and ready!")
}
