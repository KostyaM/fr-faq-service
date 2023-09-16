package faq.fastreport.ru.faq.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import java.io.File
import java.util.logging.Logger

class YamlTreeDataSource(
    private val faqTreeDatabaseSource: FaqTreeDatabaseSource,
    private val yamlMapper: ObjectMapper
) {
    fun initialize(pathToYaml: String) {
        val nodesSet = hashSetOf<AnswerNodeDto>()
        try {
            val root = yamlMapper.readTree(File(pathToYaml)).fields().next()
            readNode(yamlMapper, 0, 0, null, root.value, nodesSet)
            val inserted = faqTreeDatabaseSource.insertNodes(nodesSet.toList())
        } catch (t: Throwable) {
            t.printStackTrace()
            println("Failed to initialize with data: ${t.stackTrace}")
            // TODO normal logging
        }
    }

    private fun readNode(
        mapper: ObjectMapper,
        parentNodeId: Int?,
        nodeId: Int,
        nodeText: String?,
        node: JsonNode,
        answerNodes: HashSet<AnswerNodeDto>
    ) {
        when {
            node.isTextual -> Unit

            node.isArray -> {
                val arrayNode = node as ArrayNode
                val nodeIterator = arrayNode.iterator()
                while (nodeIterator.hasNext()) {
                    val arrayItem = nodeIterator.next()
                    if (arrayItem.isTextual) {
                        val arrayItemAsNode = mapper.readTree(arrayItem.asText())
                        val (childNodeId, childNodeName) = keyToIdValuePair(arrayItemAsNode.asText())
                        readNode(
                            mapper = mapper,
                            parentNodeId = nodeId,
                            nodeId = childNodeId,
                            nodeText = childNodeName,
                            node = arrayItemAsNode,
                            answerNodes = answerNodes
                        )
                    } else {
                        parseInnerNode(mapper, nodeId, arrayItem, answerNodes)
                    }
                }
            }

            else -> {
                parseInnerNode(mapper, nodeId, node, answerNodes)
            }
        }
        // Самый первый узел пропускаем
        if (nodeId != 0) {
            answerNodes.add(
                AnswerNodeDto(
                    id = nodeId,
                    optionText = nodeText,
                    parentId = parentNodeId
                )
            )
        }
    }

    private fun parseInnerNode(
        mapper: ObjectMapper,
        parentNodeId: Int?,
        node: JsonNode,
        answerNodes: HashSet<AnswerNodeDto>
    ) {
        val childrenIterator = node.fields()
        while (childrenIterator.hasNext()) {
            val child = childrenIterator.next()
            val (childNodeId, childNodeName) = keyToIdValuePair(child.key)
            readNode(
                mapper = mapper,
                parentNodeId = parentNodeId,
                nodeId = childNodeId,
                nodeText = childNodeName,
                node = child.value,
                answerNodes = answerNodes
            )
        }
    }

    private fun keyToIdValuePair(key: String): Pair<Int, String> {
        val nodeKeyData = key.split(",")
        val nodeIndex = nodeKeyData.first().toInt()
        val text = nodeKeyData.subList(1, nodeKeyData.size).joinToString("")
        return nodeIndex to text
    }
}
