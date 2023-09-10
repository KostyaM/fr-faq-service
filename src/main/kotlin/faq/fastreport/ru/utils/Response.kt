package faq.fastreport.ru.utils

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

suspend fun <T> PipelineContext<Unit, ApplicationCall>.safeResponse(
    objectMapper: ObjectMapper,
    block: () -> T
) {
    val response = try {
        BasicResponse(
            state = STATE_OK,
            data = block(),
            errorMessage = null
        )
    } catch (t: Throwable) {
        BasicResponse(
            state = STATE_ERROR,
            data = null,
            errorMessage = t.localizedMessage
        )
    }
    val serialized = objectMapper.writeValueAsString(response)
    println("Responding with: $serialized")
    call.respondText(serialized)
}

class BasicResponse<T>(
    val state: Int,
    val data: T?,
    val errorMessage: String?
)

const val STATE_OK = 0
const val STATE_ERROR = -1