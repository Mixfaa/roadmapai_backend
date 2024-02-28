package ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.roadmap.scaffold.prompt.StaticPromptInjectionTarget

object BuildTest : StaticPromptInjectionTarget("build_test") {
    fun makeRequest(topic:String, lecture:String) : ChatCompletionRequest {
        return this.prompt.buildChatRequest(
            mapOf(
                "(TOPIC)" to topic,
                "(TEXT)" to lecture
            )
        )
    }
}