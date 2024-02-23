package ua.torchers.roadmapai.ai.prompt.model

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage


data class PromptDescription(
    val name: String,
    val messageFormat: String,
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val topP: Double? = null
) {
    fun buildChatRequest(formatArgs: Map<String, String>): ChatCompletionRequest {
        var messageText: String = messageFormat
        for ((str, replacement) in formatArgs)
            messageText = messageText.replace(str, replacement)

        val builder = ChatCompletionRequest.builder()

        if (topP != null)
            builder.topP(topP)

        if (maxTokens != null)
            builder.maxTokens(maxTokens)

        if (temperature != null)
            builder.temperature(temperature)

        return builder
            .messages(listOf(ChatMessage("user", messageText)))
            .build()
    }

    fun equalsByName(name: String): Boolean {
        return this.name == name
    }

    fun equalsByName(other: PromptDescription): Boolean {
        return this == other || this.name == other.name
    }
}


