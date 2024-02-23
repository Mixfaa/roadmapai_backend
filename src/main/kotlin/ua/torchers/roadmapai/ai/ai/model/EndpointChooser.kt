package ua.torchers.roadmapai.ai.ai.model

interface EndpointChooser {
    fun chooseEndpointIn(service: AiService): AiService.Endpoint
}