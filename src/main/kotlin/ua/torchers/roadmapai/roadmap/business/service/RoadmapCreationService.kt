package ua.torchers.roadmapai.roadmap.business.service

import com.theokanning.openai.completion.chat.ChatCompletionResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ua.torchers.roadmapai.ai.ai.model.AiService
import ua.torchers.roadmapai.ai.ai.service.AiRequestExecutionService
import ua.torchers.roadmapai.ai.ai.service.AiServicesContainer
import ua.torchers.roadmapai.roadmap.UnclearAiAnswerException
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap
import ua.torchers.roadmapai.roadmap.scaffold.model.RoadmapDto
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.BuildRoadmap
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.ChooseLangModel
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.ConvertToJson
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.RoadmapRelevanceCheck
import ua.torchers.roadmapai.shared.EitherAny
import ua.torchers.roadmapai.shared.EitherError
import ua.torchers.roadmapai.shared.getOrThrow
import kotlin.reflect.KFunction

@Service
class RoadmapCreationService(
    private val aiServicesContainer: AiServicesContainer,
    private val aiExecutor: AiRequestExecutionService
) {
    private val miscAiService = aiServicesContainer.getServiceByName("misc")!!

    fun createRoadmap(userDescription: String): Mono<RoadmapDto> {
        return Mono.fromCallable {
            val availableServices = aiServicesContainer.listServices()

            val chosenService: AiService

            var response: EitherAny<ChatCompletionResult>
            var textResponse: String

            if (availableServices.size == 1)
                chosenService = miscAiService
            else {
                val chooseLLMRequest = ChooseLangModel.makeRequest(userDescription, availableServices)
                response = aiExecutor.executeRequest(chooseLLMRequest, miscAiService)
                textResponse = response.getOrThrow().choices.first().message.content

                chosenService = ChooseLangModel.handleResponse(textResponse, availableServices)
                    ?: miscAiService
            }

            val buildRoadmapRequest = BuildRoadmap.makeRequest(userDescription)

            response = aiExecutor.executeRequest(buildRoadmapRequest, chosenService)
            val roadmapString = response.getOrThrow().choices.first().message.content

            val checkRelevanceRequest = RoadmapRelevanceCheck.makeRequest(roadmapString)

            response = aiExecutor.executeRequest(checkRelevanceRequest, chosenService)
            textResponse = response.getOrThrow().choices.first().message.content

            val isRelevant = RoadmapRelevanceCheck.handleResponse(textResponse).getOrThrow()
            if (!isRelevant) throw UnclearAiAnswerException("Lang model $chosenService generated irrelevant roadmap $roadmapString")

            val toJsonRequest = ConvertToJson.makeRequest(roadmapString, Roadmap.JSON_SCHEMA)

            response = aiExecutor.executeRequest(toJsonRequest, miscAiService)
            textResponse = response.getOrThrow().choices.first().message.content

            ConvertToJson.handleResponse(textResponse, RoadmapDto::class.java).getOrThrow()
        }
            .publishOn(Schedulers.boundedElastic())
    }
}