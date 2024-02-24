package ua.torchers.roadmapai.ai.ai

import org.springframework.boot.context.properties.ConfigurationProperties
import ua.torchers.roadmapai.ai.ai.model.AiServiceConfig

@ConfigurationProperties("ai")
data class OnStartAiServicesConfig(
    val aiServices: List<AiServiceConfig>
)