package ua.torchers.roadmapai.roadmap.business.service

import com.theokanning.openai.completion.chat.ChatCompletionResult
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ua.torchers.roadmapai.ai.ai.model.AiService
import ua.torchers.roadmapai.ai.ai.service.AiRequestExecutionService
import ua.torchers.roadmapai.ai.ai.service.AiServicesContainer
import ua.torchers.roadmapai.roadmap.NoCachedValue
import ua.torchers.roadmapai.roadmap.UnclearAiAnswerException
import ua.torchers.roadmapai.roadmap.business.model.RoadmapCached
import ua.torchers.roadmapai.roadmap.business.model.RoadmapEntity
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap
import ua.torchers.roadmapai.roadmap.scaffold.model.RoadmapDto
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.BuildRoadmap
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.ChooseLangModel
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.ConvertToJson
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.RoadmapRelevanceCheck
import ua.torchers.roadmapai.shared.EitherAny
import ua.torchers.roadmapai.shared.getOrThrow
import java.util.*

@Service
class RoadmapCreationService(
    private val aiServicesContainer: AiServicesContainer,
    private val aiExecutor: AiRequestExecutionService,
    private val roadmapRedis: RedisTemplate<String, RoadmapCached>,
    private val roadmapRepo: RoadmapRepo
) {
    private val miscAiService: AiService = aiServicesContainer.getServiceByName("misc")!!

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

    fun createRoadmap(userDescription: String): Mono<RoadmapCached> {
        return Mono.fromCallable {
            val availableServices = aiServicesContainer.listServices()

            var chosenService: AiService = miscAiService

            var response: EitherAny<ChatCompletionResult>
            var textResponse: String

            if (availableServices.size != 1) {
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

            val createdRoadmap =
                ConvertToJson.handleResponse(textResponse, RoadmapDto::class.java, chosenService).getOrThrow()

            createdRoadmap
        }
            .map(::saveToCache)
            .publishOn(Schedulers.boundedElastic())
    }
}