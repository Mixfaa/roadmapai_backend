package ua.torchers.roadmapai.ai.ai.model

import com.theokanning.openai.client.OpenAiApi
import com.theokanning.openai.service.OpenAiService

data class AiService(
    val name: String,
    val modelId: String,
    val description: String,
    val endpoints: MutableList<Endpoint>
) {
    constructor(description: AiServiceConfig, endpoints: MutableList<Endpoint>) : this(
        description.name,
        description.modelId,
        description.description,
        endpoints
    )

    class Endpoint(val endpoint: String, api: OpenAiApi) : OpenAiService(api) {
        fun equalsByEndpoint(endpoint: String): Boolean {
            return this.endpoint == endpoint
        }

        fun equalsByEndpoint(other: Endpoint): Boolean {
            return this == other || this.endpoint == other.endpoint
        }
    }

    fun equalsByName(name: String): Boolean {
        return this.name == name
    }

    fun equalsByName(other: AiService): Boolean {
        return this == other || this.name == other.name
    }
}


