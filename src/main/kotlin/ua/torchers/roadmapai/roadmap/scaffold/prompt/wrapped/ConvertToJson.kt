package ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped

import arrow.core.Either
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.roadmap.scaffold.prompt.StaticPromptInjectionTarget
import ua.torchers.roadmapai.shared.EitherError


object ConvertToJson : StaticPromptInjectionTarget("convert_to_json") {
    private val mapper = ObjectMapper()

    fun makeRequest(data: String, jsonModel: String): ChatCompletionRequest {
        return prompt
            .buildChatRequest(mapOf("(DATA)" to data, "(JSON_MODEL)" to jsonModel))
    }

    fun <T> handleResponse(response: String, targetClass: Class<T>): EitherError<T> = Either.catch {
        require(!targetClass.isInterface)

        val firstBrace = response.indexOf('{')
        val lastBrace = response.lastIndexOf('}') + 1

        val json = response.substring(firstBrace, lastBrace)

        return mapper.readValue(json, targetClass).right()
    }
}