package faq.fastreport.ru.di

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import faq.fastreport.ru.faq.data.FaqTreeDatabaseSource
import faq.fastreport.ru.faq.data.YamlTreeDataSource
import faq.fastreport.ru.faq.routing.FaqRouting
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.ktorm.database.Database

val appModule = module {

    /* Слой конфигурации */
    single {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/postgres",
            driver = "org.postgresql.Driver",
            user = "frFaqService",
            password = "yc6t23746ch7t436x"
        )
    }

    single(YamlSerializer) {
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule.Builder().build())
        mapper
    }

    single(JsonSerializer) {
        val mapper = ObjectMapper(JsonFactory())
        mapper.registerModule(KotlinModule.Builder().build())
        mapper
    }

    /* Слой данных (Data) */

    single { FaqTreeDatabaseSource(get()) }
    single {
        YamlTreeDataSource(
            faqTreeDatabaseSource = get(),
            yamlMapper = get(YamlSerializer)
        )
    }

    /* Слой роутинга (Presentation) */

    single<FaqRouting> {
        FaqRouting(
            faqTreeDatabaseSource = get(),
            jsonMapper = get(JsonSerializer)
        )
    }
}

private val YamlSerializer = named("yaml")
private val JsonSerializer = named("yaml")