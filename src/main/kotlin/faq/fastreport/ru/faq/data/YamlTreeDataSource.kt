package faq.fastreport.ru.faq.data

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

class YamlTreeDataSource {
    val testRead = """
         root:
          100,Технический вопрос:
            - 110,VCL
            - 120,NET:
                - 121,Какие отличия между редакциями в FastReport .NET?
                - 122,Редакция Standard поставляется с минимальной функциональностью, без исходного кода и онлайн-дизайнера для редактирования отчетов в веб; <br/> Редакция Professional поставляется с исходным кодом;<br/>Редакция Enterprise поставляется с исходным кодом и онлайн дизайнером для редактирования отчетов в веб-интерфейсе.
            - 130,Cross-platform
            - 140,Services
          200,Вопрос по способам оплаты:
            - 210,Оплата физ. лцом:
                - 211,Можно оплатить картой
            - 220,Оплата юр. лицом:
                - 221,Необходимо выставить счёт
          300,Вопросы лицензирования:
            - 310,Я могу протестировать продукт перед покупкой? Вы оказываете техническую поддержку при тестировании?
                - 311,Да, конечно, мы рекомендуем протестировать наш программный продукт перед покупкой, чтобы убедиться, что он соответствует вашим требованиям. Пробная версия включает в себя все функциональные возможности полной версии, но имеет ряд ограничений. Наши специалисты будут рады помочь вам. Вы можете отправить нам технические вопросы на support@fastreport.ru.
            - 320,У меня есть идея, которая может улучшить ваш продукт. Могу ли я поделиться своими мыслями?
                - 321, Мы всегда рады узнать, что нужно нашим клиентам. Наша компания всегда открыта для новых идей и предложений. У нас есть специальный список задач (to-do list). В него мы включаем функционал, который должен быть реализован как можно быстрее. Вполне вероятно, ваша идея также окажется интересной и будет включена в него.
            - 330,Я хотел бы перейти с лицензии Single до Team.. Сколько это будет стоит?
              - >
             - 331,Переход от Single лицензии до Team возможен
              в рамках активной подписки со скидкой 20%.
    """.trimIndent()

    fun loadFromFile() {
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule.Builder().build())


        val root = mapper.readTree(testRead).fields().next()
        val nodeMap = hashMapOf<Int, AnswerNodeDto>()
        readNode(mapper, 0, null, root.value, nodeMap)
        println(nodeMap)
    }

    private fun readNode(
        mapper: ObjectMapper,
        nodeId: Int,
        nodeText: String?,
        node: JsonNode,
        answerNodes: HashMap<Int, AnswerNodeDto>
    ) {
        val childrenIds = mutableListOf<Int>()
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
                        childrenIds.add(childNodeId)
                        readNode(mapper, childNodeId, childNodeName, arrayItemAsNode, answerNodes)
                    } else {
                        parseInnerNode(mapper, arrayItem, childrenIds, answerNodes)
                    }
                }
            }

            else -> {
                parseInnerNode(mapper, node, childrenIds, answerNodes)
            }
        }
        answerNodes[nodeId] = AnswerNodeDto(
            id = nodeId,
            text = nodeText,
            childrenIds = childrenIds
        )
    }

    private fun parseInnerNode(
        mapper: ObjectMapper,
        node: JsonNode,
        childrenIds: MutableList<Int>,
        answerNodes: HashMap<Int, AnswerNodeDto>
    ) {
        val childrenIterator = node.fields()
        while (childrenIterator.hasNext()) {
            val child = childrenIterator.next()
            val (childNodeId, childNodeName) = keyToIdValuePair(child.key)
            childrenIds.add(childNodeId)
            readNode(
                mapper = mapper,
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
