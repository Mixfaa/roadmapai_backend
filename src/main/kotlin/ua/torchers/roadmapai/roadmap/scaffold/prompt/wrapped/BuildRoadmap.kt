package ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.roadmap.scaffold.prompt.StaticPromptInjectionTarget


object BuildRoadmap : StaticPromptInjectionTarget("build_roadmap") {
    fun makeRequest(request: String): ChatCompletionRequest {
        return prompt.buildChatRequest(mapOf("(LEARN_TARGET)" to request))
    }
}