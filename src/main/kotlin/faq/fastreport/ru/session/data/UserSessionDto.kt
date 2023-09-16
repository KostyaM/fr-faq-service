package faq.fastreport.ru.session.data

import java.util.UUID

data class UserSessionDto(
    val id: Long? = null,
    val userId: UUID,
    val stages: List<Int>
)