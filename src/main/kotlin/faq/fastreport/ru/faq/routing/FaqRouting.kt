package faq.fastreport.ru.faq.routing

import com.fasterxml.jackson.databind.ObjectMapper
import faq.fastreport.ru.faq.data.YamlTreeDataSource
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

class FaqRouting(
    private val yamlTreeDataSource: YamlTreeDataSource,
    private val mapper: ObjectMapper
) {

    fun configure(application: Application) = with(application) {
        routing {
            get("/faq/{id}") {
                getById()
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.getById() {
        val nodeMap = yamlTreeDataSource.getNodeMap()
        val idParam = call.parameters["id"]?.toInt()
        val answer = if (idParam == null) {
            nodeMap[0]
        } else {
            nodeMap[idParam]
        }
        call.respondText(mapper.writeValueAsString(answer))
    }

}