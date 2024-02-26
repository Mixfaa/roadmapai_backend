package ua.torchers.roadmapai.test

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.roadmap.business.model.RoadmapCached
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap
import ua.torchers.roadmapai.roadmap.scaffold.service.RoadmapCreationService


@RestController
@RequestMapping("/ai")
class TestController(
    private val roadmapCreationService: RoadmapCreationService
) {

    @GetMapping("/roadmap")
    fun getRoadmap(learningTarget: String): Mono<out Roadmap> {
        return roadmapCreationService.createRoadmap(learningTarget)
    }

}