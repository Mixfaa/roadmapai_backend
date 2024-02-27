package ua.torchers.roadmapai.ai.ai.service

import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import ua.torchers.roadmapai.ai.ai.model.AiService
import ua.torchers.roadmapai.ai.ai.model.AiServiceEvent
import ua.torchers.roadmapai.ai.ai.model.EndpointChooser
import java.util.*

@Component
class LoopEndpointChooser : EndpointChooser, ApplicationListener<AiServiceEvent> {
    private val lastUsedEndpoints: MutableMap<AiService, Int> = Collections.synchronizedMap(HashMap())

    override fun chooseEndpointIn(service: AiService): AiService.Endpoint {
        val index = lastUsedEndpoints.compute(service) { service2, index ->
            if (index == null) return@compute 0

            var newIndex = index + 1
            if (newIndex >= service2.endpoints.size)
                newIndex = 0

            return@compute newIndex
        }
        return service.endpoints[index ?: 0]
    }

    override fun onApplicationEvent(event: AiServiceEvent) {
        when (event) {
            is AiServiceEvent.ServiceAdded -> {}
            is AiServiceEvent.ServiceRemoved -> {
                lastUsedEndpoints.remove(event.service)
            }

            is AiServiceEvent.EndpointAdded -> {
                lastUsedEndpoints.remove(event.service)
            }

            is AiServiceEvent.EndpointRemoved -> {
                lastUsedEndpoints.remove(event.service)
            }
        }
    }
}