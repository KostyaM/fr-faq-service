package faq.fastreport.ru.session.domain

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import faq.fastreport.ru.session.data.UserSessionDto
import faq.fastreport.ru.session.data.UserSessionsDataSource
import io.ktor.server.application.*
import java.util.*

class SessionCreationUseCase(
    private val application: Application,
    private val userSessionsDataSource: UserSessionsDataSource
) {
    fun createUserSession(): String {
        val configuration = application.environment.config
        var userId: UUID = UUID.randomUUID()
        while (userSessionsDataSource.isUserExists(userId)) {
            userId = UUID.randomUUID()
        }

        val newToken = with(configuration) {
            val algorithm = Algorithm.HMAC512(property("jwt.privateKey").getString())
            JWT.create()
                .withSubject("Authentication")
                .withAudience(property("jwt.audience").getString())
                .withIssuer(property("jwt.issuer").getString())
                .withClaim("userId", userId.toString())
                .sign(algorithm)
        }
        val userSession = UserSessionDto(
            userId = userId,
            stages = listOf(0)
        )
        userSessionsDataSource.createSession(userSession)
        return newToken
    }

    fun verifySession(): JWTVerifier {
        val configuration = application.environment.config
        return with(configuration) {
            val audience = property("jwt.audience").getString()
            val issuer = property("jwt.issuer").getString()
            val algorithm = Algorithm.HMAC512(property("jwt.privateKey").getString())
            JWT.require(algorithm)
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
        }
    }
}