package ua.torchers.roadmapai.roadmap.prompt.wrapped

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.roadmap.prompt.StaticAiPromptInjectionTarget


object RemoveExtra : StaticAiPromptInjectionTarget("remove_extra") {
    fun makeRequest(text: String): ChatCompletionRequest {
        return prompt.buildChatRequest(mapOf("(TEXT)" to text))
    }
}