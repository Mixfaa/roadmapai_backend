package ua.torchers.roadmapai.roadmap.prompt.wrapped

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.roadmap.prompt.StaticAiPromptInjectionTarget


object BuildRoadmap : StaticAiPromptInjectionTarget("build_roadmap") {
    fun makeRequest(request: String): ChatCompletionRequest {
        return prompt.buildChatRequest(mapOf("(LEARN_TARGET)" to request))
    }
}