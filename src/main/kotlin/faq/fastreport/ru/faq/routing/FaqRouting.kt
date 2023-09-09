package faq.fastreport.ru.faq.routing

import com.fasterxml.jackson.databind.ObjectMapper
import faq.fastreport.ru.faq.data.FaqTreeDatabaseSource
import faq.fastreport.ru.utils.safeResponse
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

class FaqRouting(
    private val faqTreeDatabaseSource: FaqTreeDatabaseSource,
    private val jsonMapper: ObjectMapper
) {

    fun configure(application: Application) = with(application) {
        routing {
            get("/faq/{id}") { getById() }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.getById() = safeResponse(jsonMapper) {
        val parentId = call.parameters["id"]?.toInt() ?: 0
        println("getById parentId: $parentId")
        faqTreeDatabaseSource.getChildren(parentId)
    }

}