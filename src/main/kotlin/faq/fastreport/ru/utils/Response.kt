package faq.fastreport.ru.utils

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

suspend inline fun <T> PipelineContext<Unit, ApplicationCall>.safeResponse(
    objectMapper: ObjectMapper,
    block: () -> T
) {
    val (response, statusCode) = try {
        BasicResponse(
            state = STATE_OK,
            data = block(),
            errorMessage = null
        ) to HttpStatusCode.OK
    } catch (t: Throwable) {
        BasicResponse(
            state = STATE_ERROR,
            data = null,
            errorMessage = t.localizedMessage
        ) to HttpStatusCode.InternalServerError
    }
    val serialized = objectMapper.writeValueAsString(response)
    call.respondText(serialized, ContentType.Application.Json, statusCode)
}

class BasicResponse<T>(
    val state: Int,
    val data: T?,
    val errorMessage: String?
)

const val STATE_OK = 0
const val STATE_ERROR = -1
const val STATE_NOT_AUTHORIZED = 401