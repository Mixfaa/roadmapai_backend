package ua.torchers.roadmapai.roadmap.prompt.wrapped

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.roadmap.prompt.StaticPromptInjectionTarget


object BuildRoadmap : StaticPromptInjectionTarget("build_roadmap") {
    fun makeRequest(request: String): ChatCompletionRequest {
        return prompt.buildChatRequest(mapOf("(LEARN_TARGET)" to request))
    }
}