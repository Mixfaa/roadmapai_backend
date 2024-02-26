package ua.torchers.roadmapai.roadmap.scaffold.service

import arrow.core.Either
import arrow.core.getOrElse
import com.theokanning.openai.completion.chat.ChatCompletionResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ua.torchers.roadmapai.ai.ai.model.AiService
import ua.torchers.roadmapai.ai.ai.service.AiRequestExecutionService
import ua.torchers.roadmapai.ai.ai.service.AiServicesContainer
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap
import ua.torchers.roadmapai.roadmap.scaffold.model.RoadmapDto
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.BuildRoadmap
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.ChooseLangModel
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.ConvertToJson
import ua.torchers.roadmapai.shared.EitherError

/*

    fun saveFromCache(id: String): Mono<RoadmapEntity> {
        val roadmapCached = roadmapRedis.opsForList().leftPop(id)
            ?: return Mono.error(NoCachedValue("Can`t find cached roadmap with id $id"))

        return roadmapRepo.save(RoadmapEntity(roadmapCached))
    }

    private fun saveToCache(roadmap: RoadmapDto): RoadmapCached {
        val uniqueId = UUID.randomUUID()
        val roadmapCached = RoadmapCached(roadmap, uniqueId)

        roadmapRedis.opsForList().leftPush(roadmapCached.id, roadmapCached)
        return roadmapCached
    }

 */

@Service
class RoadmapCreationService(
    private val aiServicesContainer: AiServicesContainer,
    private val aiExecutor: AiRequestExecutionService,
) {
    private val miscAiService: AiService = aiServicesContainer.getServiceByName("misc")!!

    fun createRoadmap(userDescription: String): Mono<RoadmapDto> {
        return Mono.fromCallable {
            Either.catch {
                val availableServices = aiServicesContainer.listServices()

                var chosenService: AiService = miscAiService

                var response: EitherError<ChatCompletionResult>
                var textResponse: String

                if (availableServices.size != 1) {
                    val chooseLLMRequest = ChooseLangModel.makeRequest(userDescription, availableServices)
                    response = aiExecutor.executeRequest(chooseLLMRequest, miscAiService)
                    textResponse = response.getOrElse { return@fromCallable response }
                        .choices.first().message.content

                    chosenService = ChooseLangModel.handleResponse(textResponse, availableServices)
                        ?: miscAiService
                }

                val buildRoadmapRequest = BuildRoadmap.makeRequest(userDescription)

                response = aiExecutor.executeRequest(buildRoadmapRequest, chosenService)

                val roadmapString = response.getOrElse { return@fromCallable response }
                    .choices.first().message.content


                val toJsonRequest = ConvertToJson.makeRequest(roadmapString, Roadmap.JSON_SCHEMA)

                response = aiExecutor.executeRequest(toJsonRequest, miscAiService)
                textResponse = response.getOrElse { return@fromCallable response }
                    .choices.first().message.content

                ConvertToJson.handleResponse(textResponse, RoadmapDto::class.java, chosenService)
            }
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
}