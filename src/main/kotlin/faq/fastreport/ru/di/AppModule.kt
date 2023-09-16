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
import org.ktorm.database.SqlDialect


private val YamlSerializer = named("yaml")
private val JsonSerializer = named("json")

val appModule = module {

    /* Слой конфигурации */
    single {
        Database.connect(
            url = "jdbc:postgresql://host.docker.internal:5432/fr_faq_db?user=frFaqService",
            driver = "org.postgresql.Driver",
            user = "frFaqService",
            password = "yc6t23746ch7t436x"
        )
    }

    single<ObjectMapper>(YamlSerializer) {
        ObjectMapper(YAMLFactory()).apply {
            registerModule(KotlinModule.Builder().build())
        }
    }

    single<ObjectMapper>(JsonSerializer) {
        ObjectMapper(JsonFactory()).apply {
            registerModule(KotlinModule.Builder().build())
        }
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