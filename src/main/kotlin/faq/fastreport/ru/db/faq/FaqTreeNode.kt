package faq.fastreport.ru.db.faq


import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

interface FaqTreeNode : Entity<FaqTreeNode> {
    companion object : Entity.Factory<FaqTreeNode>()

    val id: Int?
    val optionText: String
    val parentId: Int?
}

object FaqTreeNodes : Table<FaqTreeNode>("faq_tree_node") {
    val id = int("id").primaryKey().bindTo(FaqTreeNode::id)
    val optionText = text("option_text").bindTo(FaqTreeNode::optionText)
    val parentId = int("parent_id").bindTo(FaqTreeNode::parentId)
}