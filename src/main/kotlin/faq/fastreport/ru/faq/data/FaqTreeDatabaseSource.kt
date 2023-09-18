package faq.fastreport.ru.faq.data

import faq.fastreport.ru.db.faq.FaqTreeNodes
import org.ktorm.database.Database
import org.ktorm.dsl.*

class FaqTreeDatabaseSource(private val database: Database) {
    fun insertNodes(nodes: List<AnswerNodeDto>): IntArray {
        return database.batchInsert(FaqTreeNodes) {
            nodes.forEach { node ->
                item { row ->
                    set(row.id, node.id)
                    set(row.optionText, node.optionText)
                    set(row.parentId, node.parentId)
                }
            }
        }
    }

    fun insertNode(node: AnswerNodeDto) {
        database.insert(FaqTreeNodes) {
            set(FaqTreeNodes.id, node.id)
            set(FaqTreeNodes.optionText, node.optionText)
            set(FaqTreeNodes.parentId, node.parentId)
        }
    }

    fun getChildren(parentId: Int): List<AnswerNodeDto> {
        return database.from(FaqTreeNodes).select()
            .where { FaqTreeNodes.parentId eq parentId }
            .map { row ->
                AnswerNodeDto(
                    id = row[FaqTreeNodes.id]!!,
                    optionText = row[FaqTreeNodes.optionText],
                    parentId = row[FaqTreeNodes.parentId]
                )
            }
    }

    fun getNodeChain(nodeIds: List<Int>): List<AnswerNodeDto> {
        return database.from(FaqTreeNodes).select()
            .where { FaqTreeNodes.id inList nodeIds }
            .orderBy(FaqTreeNodes.id.asc())
            .map { row ->
                AnswerNodeDto(
                    id = row[FaqTreeNodes.id]!!,
                    optionText = row[FaqTreeNodes.optionText],
                    parentId = row[FaqTreeNodes.parentId]
                )
            }
    }

    fun getAll(): List<AnswerNodeDto> {
        return database.from(FaqTreeNodes).select()
            .orderBy(FaqTreeNodes.id.asc())
            .map { row ->
                AnswerNodeDto(
                    id = row[FaqTreeNodes.id]!!,
                    optionText = row[FaqTreeNodes.optionText],
                    parentId = row[FaqTreeNodes.parentId]
                )
            }
    }
}