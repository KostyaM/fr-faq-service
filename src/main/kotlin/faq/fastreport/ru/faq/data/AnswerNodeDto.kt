package faq.fastreport.ru.faq.data

data class AnswerNodeDto(
    val id: Int,
    val text: String?,
    val childrenIds: List<Int>
)