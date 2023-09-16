package faq.fastreport.ru

import faq.fastreport.ru.di.appModule
import faq.fastreport.ru.faq.data.YamlTreeDataSource
import faq.fastreport.ru.faq.routing.FaqRouting
import io.ktor.server.application.*
import io.ktor.server.netty.*
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

    // Роутинг /faq
    val faqRouting by inject<FaqRouting>()
    faqRouting.configure(this)
    log.info("Configured and ready!")
}
