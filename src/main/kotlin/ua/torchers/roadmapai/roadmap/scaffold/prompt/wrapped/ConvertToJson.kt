package ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.TextNode
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.ai.UnclearAiAnswerException
import ua.torchers.roadmapai.ai.ai.model.AiService
import ua.torchers.roadmapai.roadmap.scaffold.model.RoadmapDto
import ua.torchers.roadmapai.roadmap.scaffold.model.TestDto
import ua.torchers.roadmapai.roadmap.scaffold.prompt.StaticPromptInjectionTarget
import ua.torchers.roadmapai.shared.EitherError
import java.util.*


object ConvertToJson : StaticPromptInjectionTarget("convert_to_json") {
    private val mapper = ObjectMapper()

    fun makeRequest(data: String, jsonModel: String): ChatCompletionRequest {
        return prompt
            .buildChatRequest(mapOf("(DATA)" to data, "(JSON_MODEL)" to jsonModel))
    }

    private fun isolateJson(response: String): String? {
        val firstBrace = response.indexOf('{')
        if (firstBrace == -1) return null

        val lastBrace = response.lastIndexOf('}') + 1
        if (lastBrace == 0) return null

        return response.substring(firstBrace, lastBrace)
    }

    fun parseRoadmap(response: String, usedService: AiService): EitherError<RoadmapDto> {
        val json = isolateJson(response)
            ?: return UnclearAiAnswerException("Can`t isolate json from ai answer ($response)").left()

        val jsonTree = mapper.readTree(json)
        val props = jsonTree.properties()

        props.add(AbstractMap.SimpleEntry("usedService", TextNode(usedService.name)) as Map.Entry<String, JsonNode>?)

        return mapper.readValue(jsonTree.toPrettyString(), RoadmapDto::class.java).right()
    }

    fun parseTest(response: String): EitherError<TestDto> {
        val json = isolateJson(response)
            ?: return UnclearAiAnswerException("Can`t isolate json from ai answer ($response)").left()

        return mapper.readValue(json, TestDto::class.java).right()
    }

    fun <T> handleResponse(
        response: String,
        targetClass: Class<T>,
        otherProps: Map<String, JsonNode>? = null
    ): EitherError<T> =
        Either.catch {
            require(!targetClass.isInterface)

            val firstBrace = response.indexOf('{')
            val lastBrace = response.lastIndexOf('}') + 1

            val json = response.substring(firstBrace, lastBrace)

            if (otherProps != null) {
                val jsonTree = mapper.readTree(json)
                val props = jsonTree.properties()
                for ((key, value) in otherProps.entries)
                    props.add(AbstractMap.SimpleEntry(key, value) as Map.Entry<String, JsonNode>?)

                mapper.readValue(jsonTree.toPrettyString(), targetClass)
            } else
                mapper.readValue(json, targetClass)
        }
}