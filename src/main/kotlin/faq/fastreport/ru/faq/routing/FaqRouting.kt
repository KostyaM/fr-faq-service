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
            get("/faq") {
                getById(0)
            }
            get("/faq/{id}") {
                val parentId = call.parameters["id"]?.toInt() ?: 0
                getById(parentId)
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.getById(parentId: Int) = safeResponse(jsonMapper) {
        println("getById parentId: $parentId")
        faqTreeDatabaseSource.getChildren(parentId)
    }
}