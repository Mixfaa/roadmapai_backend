package ua.torchers.roadmapai.ai.ai.model

import org.springframework.context.ApplicationEvent

sealed class AiServiceEvent(source: Any) : ApplicationEvent(source) {
    class ServiceAdded(src: Any, val service: AiService) : AiServiceEvent(src)
    class ServiceRemoved(src: Any, val service: AiService) : AiServiceEvent(src)
    class EndpointAdded(src: Any, val service: AiService, val endpoint: AiService.Endpoint) : AiServiceEvent(src)
    class EndpointRemoved(src: Any, val service: AiService, val endpoint: AiService.Endpoint) : AiServiceEvent(src)
}