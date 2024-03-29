package ua.torchers.roadmapai.roadmap.scaffold.service

import arrow.core.getOrElse
import arrow.core.left
import com.theokanning.openai.completion.chat.ChatCompletionResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ua.torchers.roadmapai.ai.NoChoicesFromAi
import ua.torchers.roadmapai.ai.ai.model.AiService
import ua.torchers.roadmapai.ai.ai.service.AiRequestExecutionService
import ua.torchers.roadmapai.ai.ai.service.AiServicesContainer
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap
import ua.torchers.roadmapai.roadmap.scaffold.model.RoadmapDto
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.BuildRoadmap
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.ChooseLangModel
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.ConvertToJson
import ua.torchers.roadmapai.shared.EitherError
import ua.torchers.roadmapai.shared.firstChoiceText

@Service
class RoadmapCreationService(
    private val aiServicesContainer: AiServicesContainer,
    private val aiExecutor: AiRequestExecutionService,
) {
    private val miscAiService: AiService = aiServicesContainer.getServiceByName("misc")!!

    fun createRoadmap(userDescription: String): Mono<RoadmapDto> = Mono.fromCallable {
        val availableServices = aiServicesContainer.listServices()

        var chosenService: AiService = miscAiService

        var response: EitherError<ChatCompletionResult>
        var textResponse: String

        // select llm for generating roadmap
        if (availableServices.size != 1) {
            val chooseLLMRequest = ChooseLangModel.makeRequest(userDescription, availableServices)

            response = aiExecutor.executeRequest(chooseLLMRequest, miscAiService)

            textResponse = response.getOrElse { return@fromCallable response }
                .firstChoiceText()
                ?: return@fromCallable NoChoicesFromAi(miscAiService).left()

            chosenService = ChooseLangModel.handleResponse(textResponse, availableServices)
                ?: miscAiService
        }

        // generating roadmap
        val buildRoadmapRequest = BuildRoadmap.makeRequest(userDescription)

        response = aiExecutor.executeRequest(buildRoadmapRequest, chosenService)

        val roadmapString = response.getOrElse { return@fromCallable response }
            .firstChoiceText()
            ?: return@fromCallable NoChoicesFromAi(chosenService).left()

        // converting roadmap to json
        val toJsonRequest = ConvertToJson.makeRequest(roadmapString, Roadmap.JSON_SCHEMA)

        response = aiExecutor.executeRequest(toJsonRequest, miscAiService)
        textResponse = response.getOrElse { return@fromCallable response }
            .firstChoiceText()
            ?: return@fromCallable NoChoicesFromAi(miscAiService).left()

        return@fromCallable ConvertToJson.parseRoadmap(textResponse, chosenService)
    }
        .flatMap {
            it.fold(
                { error ->
                    Mono.error(error)
                },
                { roadmap ->
                    when (roadmap) {
                        is RoadmapDto -> Mono.just(roadmap)
                        else -> Mono.error(IllegalArgumentException("Not a RoadmapDto object, but (${roadmap::class})"))
                    }
                })
        }
        .publishOn(Schedulers.boundedElastic())

}