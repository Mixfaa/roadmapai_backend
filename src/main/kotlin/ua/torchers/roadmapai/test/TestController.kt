package ua.torchers.roadmapai.test

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.roadmap.business.service.RoadmapCreationService
import ua.torchers.roadmapai.roadmap.scaffold.model.RoadmapDto


@RestController
@RequestMapping("/ai")
class TestController(
    private val roadmapCreationService: RoadmapCreationService
) {

    @GetMapping("/roadmap")
    fun getRoadmap(learningTarget: String): Mono<RoadmapDto> {
        return roadmapCreationService.createRoadmap(learningTarget)
    }

}