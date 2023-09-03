package faq.fastreport.ru.di

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import faq.fastreport.ru.faq.data.YamlTreeDataSource
import faq.fastreport.ru.faq.routing.FaqRouting
import io.ktor.server.application.*
import org.koin.dsl.module

val appModule = module {
    single<ObjectMapper> {
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule.Builder().build())
        mapper
    }
    single<YamlTreeDataSource> { YamlTreeDataSource(get()) }

    single<FaqRouting> { FaqRouting(get(), get()) }
}