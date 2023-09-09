package faq.fastreport.ru.faq.data

class AnswerNodeDto(
    val id: Int,
    val optionText: String?,
    val parentId: Int?
) {
    override fun hashCode(): Int {
        return id
    }

    override fun equals(other: Any?): Boolean {
        val otherAnswer = other as? AnswerNodeDto ?: return false
        return this.id == other.id &&
                this.optionText == other.optionText &&
                this.parentId == other.parentId
    }
}