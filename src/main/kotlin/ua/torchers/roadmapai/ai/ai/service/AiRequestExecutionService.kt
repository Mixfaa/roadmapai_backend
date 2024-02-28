package ua.torchers.roadmapai.ai.ai.service

import arrow.core.Either
import arrow.core.left
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatCompletionResult
import org.springframework.stereotype.Service
import ua.torchers.roadmapai.ai.NoServiceException
import ua.torchers.roadmapai.ai.ai.model.AiService
import ua.torchers.roadmapai.ai.ai.model.EndpointChooser
import ua.torchers.roadmapai.shared.EitherError

@Service
class AiRequestExecutionService(
    private val aiServicesContainer: AiServicesContainer,
    private val endpointChooser: EndpointChooser
) {
    fun executeRequest(request: ChatCompletionRequest, serviceName: String): EitherError<ChatCompletionResult> {
        val service = aiServicesContainer.getServiceByName(serviceName)
            ?: return NoServiceException(serviceName).left()

        return executeRequest(request, service)
    }

    fun executeRequest(request: ChatCompletionRequest, service: AiService): EitherError<ChatCompletionResult> =
        Either.catch {
            val endpoint = endpointChooser.chooseEndpointIn(service)
            request.model = service.modelId
            endpoint.createChatCompletion(request)
        }

}