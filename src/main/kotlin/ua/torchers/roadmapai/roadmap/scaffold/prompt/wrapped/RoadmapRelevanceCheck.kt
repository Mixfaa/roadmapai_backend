package ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped

import arrow.core.left
import arrow.core.right
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.roadmap.UnclearAiAnswerException
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap
import ua.torchers.roadmapai.roadmap.scaffold.prompt.StaticPromptInjectionTarget
import ua.torchers.roadmapai.shared.EitherError

object RoadmapRelevanceCheck : StaticPromptInjectionTarget("roadmap_check_relevance") {

    fun makeRequest(roadmap: String): ChatCompletionRequest {
        return prompt.buildChatRequest(mapOf("(ROADMAP)" to roadmap))
    }

    fun makeRequest(roadmap: Roadmap): ChatCompletionRequest {
        return prompt.buildChatRequest(mapOf("(ROADMAP)" to roadmap.stringForAi()))
    }

    fun handleResponse(response: String): EitherError<Boolean> {
        val isRelevant = response.contains("relevant")
        val isInvalid = response.contains("invalid")

        if (isRelevant && !isInvalid) return true.right()
        if (!isRelevant && isInvalid) return false.right()

        return UnclearAiAnswerException("$targetPromptName Can`t define result. Ai answer: $response").left()
    }
}
