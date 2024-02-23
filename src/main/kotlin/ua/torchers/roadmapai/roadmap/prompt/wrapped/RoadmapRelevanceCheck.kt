package ua.torchers.roadmapai.roadmap.prompt.wrapped

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.roadmap.UnclearAiAnswerException
import ua.torchers.roadmapai.roadmap.model.Roadmap
import ua.torchers.roadmapai.roadmap.prompt.StaticAiPromptInjectionTarget
import ua.torchers.roadmapai.shared.EitherError

object RoadmapRelevanceCheck : StaticAiPromptInjectionTarget("roadmap_check_relevance") {
    fun makeRequest(roadmap: Roadmap): EitherError<ChatCompletionRequest> = Either.catch {
        prompt.buildChatRequest(mapOf("(ROADMAP)" to roadmap.stringForAi()))
    }

    fun handleResponse(response: String): EitherError<Boolean> {
        val isRelevant = response.contains("relevant")
        val isInvalid = response.contains("invalid")

        if (isRelevant && !isInvalid) return true.right()
        if (!isRelevant && isInvalid) return false.right()

        return UnclearAiAnswerException("$targetPromptName Can`t define result. Ai answer: $response").left()
    }
}
