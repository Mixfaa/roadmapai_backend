package ua.torchers.roadmapai.roadmap.scaffold.service

import arrow.core.getOrElse
import arrow.core.left
import com.theokanning.openai.completion.chat.ChatCompletionResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ua.torchers.roadmapai.ai.NoChoicesFromAi
import ua.torchers.roadmapai.ai.NoServiceException
import ua.torchers.roadmapai.ai.ai.service.AiRequestExecutionService
import ua.torchers.roadmapai.ai.ai.service.AiServicesContainer
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap
import ua.torchers.roadmapai.roadmap.scaffold.model.TestDto
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.BuildTest
import ua.torchers.roadmapai.roadmap.scaffold.prompt.wrapped.ConvertToJson
import ua.torchers.roadmapai.shared.EitherError
import ua.torchers.roadmapai.shared.firstChoiceText

@Service
class TestCreationService(
    private val aiServicesContainer: AiServicesContainer,
    private val aiExecutionService: AiRequestExecutionService
) {
    private val miscAiService = aiServicesContainer.getServiceByName("misc")!!

    fun generateTestFor(roadmap: Roadmap, lecture: Roadmap.RmContent): Mono<TestDto> {
        return Mono.fromCallable {
            var response: EitherError<ChatCompletionResult>

            val usedService = aiServicesContainer.getServiceByName(roadmap.usedService)
                ?: return@fromCallable NoServiceException(roadmap.usedService).left()

            // building test
            val buildTestRequest = BuildTest.makeRequest(roadmap.name, lecture.content)
            response = aiExecutionService.executeRequest(buildTestRequest, usedService)
            val testText = response.getOrElse { return@fromCallable response }
                .firstChoiceText()
                ?: return@fromCallable NoChoicesFromAi(usedService).left()

            val toJsonRequest = ConvertToJson.makeRequest(testText, TestDto.JSON_SCHEMA)
            response = aiExecutionService.executeRequest(toJsonRequest, miscAiService)
            val jsonText = response.getOrElse { return@fromCallable response }
                .firstChoiceText()
                ?: return@fromCallable NoChoicesFromAi(usedService).left()

            return@fromCallable ConvertToJson.parseTest(jsonText)
        }
            .flatMap {
                it.fold({ error ->
                    Mono.error(error)
                }, { testDto ->
                    when (testDto) {
                        is TestDto -> Mono.just(testDto)
                        else -> Mono.error(IllegalArgumentException("Not a TestDto object, but (${testDto::class})"))
                    }
                })
            }
            .publishOn(Schedulers.boundedElastic())
    }
}