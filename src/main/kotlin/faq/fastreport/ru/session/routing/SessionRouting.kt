package faq.fastreport.ru.session.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import faq.fastreport.ru.faq.data.FaqTreeDatabaseSource
import faq.fastreport.ru.session.data.UserSessionsDataSource
import faq.fastreport.ru.session.domain.SessionCreationUseCase
import faq.fastreport.ru.utils.BasicResponse
import faq.fastreport.ru.utils.STATE_NOT_AUTHORIZED
import faq.fastreport.ru.utils.safeResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*


class SessionRouting(
    private val application: Application,
    private val jsonMapper: ObjectMapper,
    private val sessionCreationUseCase: SessionCreationUseCase,
    private val userSessionsDataSource: UserSessionsDataSource
) {
    fun configure() = with(application) {
        val serviceRealm = environment.config.property("jwt.realm").getString()
        install(Authentication) {
            jwt("auth-jwt") {
                realm = serviceRealm
                verifier(sessionCreationUseCase.verifySession())
                validate { credential ->
                    try {
                        UUID.fromString(credential.payload.getClaim("userId").asString())
                        JWTPrincipal(credential.payload)
                    } catch (exception: IllegalArgumentException) {
                        null
                    }
                }
                challenge { defaultScheme, realm ->
                    val response = BasicResponse(
                        state = STATE_NOT_AUTHORIZED,
                        data = null,
                        errorMessage = "Token is not valid or not specified"
                    )
                    call.respond(HttpStatusCode.Unauthorized, response)
                }
            }
        }
        routing {
            get("/session/new") {
                safeResponse(jsonMapper) {
                    sessionCreationUseCase.createUserSession()
                }
            }
            authenticate("auth-jwt") {
                post("/session/reset") {
                    safeResponse(jsonMapper) {
                        val principal = call.principal<JWTPrincipal>()
                        val userId = UUID.fromString(principal!!.payload.getClaim("userId").asString())
                        userSessionsDataSource.setStages(userId, listOf(0))
                        userSessionsDataSource.getSession(userId)
                    }
                }
            }
        }
    }
}