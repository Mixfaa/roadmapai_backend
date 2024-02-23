package ua.torchers.roadmapai.roadmap.prompt.wrapped

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import ua.torchers.roadmapai.ai.ai.model.AiService
import ua.torchers.roadmapai.roadmap.prompt.StaticAiPromptInjectionTarget

object ChooseLangModel : StaticAiPromptInjectionTarget("choose_llm") {

    fun makeRequest(request: String, services: List<AiService>): ChatCompletionRequest {
        return prompt.buildChatRequest(
            mapOf(
                "(REQUEST)" to request,
                "(SERVICES)" to buildString {
                    for (service in services) {
                        this.append("Service:").appendLine(service.modelId)
                        this.appendLine(service.description)
                    }
                }
            )
        )
    }

    fun handleResponse(answer: String, services: List<AiService>): AiService? {
        return services.find { answer.contains(it.modelId) }
    }
}