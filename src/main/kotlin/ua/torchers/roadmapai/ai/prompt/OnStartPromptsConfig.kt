package ua.torchers.roadmapai.ai.prompt

import org.springframework.boot.context.properties.ConfigurationProperties
import ua.torchers.roadmapai.ai.prompt.model.PromptDescription

@ConfigurationProperties("prompt")
data class OnStartPromptsConfig(
    val prompts: List<PromptDescription>
)

