package faq.fastreport.ru.di

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import faq.fastreport.ru.faq.data.FaqTreeDatabaseSource
import faq.fastreport.ru.faq.data.YamlTreeDataSource
import faq.fastreport.ru.faq.routing.FaqRouting
import faq.fastreport.ru.session.data.UserSessionsDataSource
import faq.fastreport.ru.session.domain.SessionCreationUseCase
import faq.fastreport.ru.session.routing.SessionRouting
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.ktorm.database.Database
import org.ktorm.database.SqlDialect


private val YamlSerializer = named("yaml")
private val JsonSerializer = named("json")

fun appModule(application: Application) = module {

    /* Слой конфигурации */
    single {
        Database.connect(
            url = "jdbc:postgresql://host.docker.internal:5432/fr_faq_db",
            driver = "org.postgresql.Driver",
            user = application.environment.config.property("database.databaseUser").getString(),
            password = application.environment.config.property("database.postgresPassword").getString()
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
    single { UserSessionsDataSource(get()) }

    /*  Domain  */

    single { SessionCreationUseCase(application, get()) }

    /* Слой роутинга (Presentation) */

    single<FaqRouting> {
        FaqRouting(
            faqTreeDatabaseSource = get(),
            jsonMapper = get(JsonSerializer),
            application = application,
            userSessionsDataSource = get()
        )
    }

    single {
        SessionRouting(
            application = application,
            jsonMapper = get(JsonSerializer),
            sessionCreationUseCase = get(),
            userSessionsDataSource = get()
        )
    }
}