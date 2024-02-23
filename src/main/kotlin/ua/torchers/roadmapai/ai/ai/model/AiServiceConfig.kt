package ua.torchers.roadmapai.ai.ai.model

data class AiServiceConfig(
    val name: String,
    val modelId: String,
    val description: String,
    val endpoints: List<EndpointConfig>
) {
    data class EndpointConfig(
        val endpoint: String,
        val token: String,
        val timeoutInMin: Long
    )
}
