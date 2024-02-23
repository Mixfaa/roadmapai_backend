package ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.roadmap.scaffold.prompt.StaticPromptInjectionTarget


object RemoveExtra : StaticPromptInjectionTarget("remove_extra") {
    fun makeRequest(text: String): ChatCompletionRequest {
        return prompt.buildChatRequest(mapOf("(TEXT)" to text))
    }
}