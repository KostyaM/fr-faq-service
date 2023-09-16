package faq.fastreport.ru.faq.routing

import com.fasterxml.jackson.databind.ObjectMapper
import faq.fastreport.ru.faq.data.FaqTreeDatabaseSource
import faq.fastreport.ru.session.data.UserSessionsDataSource
import faq.fastreport.ru.utils.safeResponse
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import java.util.*

class FaqRouting(
    private val application: Application,
    private val faqTreeDatabaseSource: FaqTreeDatabaseSource,
    private val userSessionsDataSource: UserSessionsDataSource,
    private val jsonMapper: ObjectMapper
) {

    fun configure() = with(application) {
        routing {
            authenticate("auth-jwt") {
                get("/faq/get") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = UUID.fromString(principal!!.payload.getClaim("userId").asString())
                    val stages = userSessionsDataSource.getSession(userId)?.stages ?: listOf(0)
                    faqTreeDatabaseSource.getNodeChain(stages)
                    getById(0)
                }
            }
            authenticate("auth-jwt") {
                get("/faq/last") {
                    safeResponse(jsonMapper) {
                        val principal = call.principal<JWTPrincipal>()
                        val userId = UUID.fromString(principal!!.payload.getClaim("userId").asString())
                        val stages = userSessionsDataSource.getSession(userId)?.stages ?: listOf(0)
                        faqTreeDatabaseSource.getNodeChain(stages)
                    }
                }
            }
            authenticate("auth-jwt") {
                get("/faq/get/{id}") {
                    val principal = call.principal<JWTPrincipal>()
                    val parentId = call.parameters["id"]?.toInt() ?: 0
                    val userId = UUID.fromString(principal!!.payload.getClaim("userId").asString())
                    val stages = userSessionsDataSource.getSession(userId)?.stages ?: listOf(0)
                    userSessionsDataSource.setStages(userId, stages + listOf(parentId))
                    getById(parentId)
                }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.getById(parentId: Int) = safeResponse(jsonMapper) {
        faqTreeDatabaseSource.getChildren(parentId)
    }
}