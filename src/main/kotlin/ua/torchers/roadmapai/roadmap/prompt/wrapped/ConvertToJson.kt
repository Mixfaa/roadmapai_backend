package ua.torchers.roadmapai.roadmap.prompt.wrapped

import arrow.core.Either
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.roadmap.prompt.StaticPromptInjectionTarget
import ua.torchers.roadmapai.shared.EitherError


object ConvertToJson : StaticPromptInjectionTarget("convert_to_json") {
    private val mapper = ObjectMapper()

    fun makeRequest(data: String, jsonModel: String): ChatCompletionRequest {
        return prompt
            .buildChatRequest(mapOf("(DATA)" to data, "(JSON_MODEL)" to jsonModel))
    }

    fun <T> handleResponse(response: String, targetClass: Class<T>): EitherError<T> = Either.catch {
        val firstBrace = response.indexOf('{')
        val lastBrace = response.lastIndexOf('}')

        val json = response.substring(firstBrace, lastBrace + 1)

        return mapper.readValue(json, targetClass).right()
    }
}