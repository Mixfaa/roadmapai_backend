package ua.torchers.roadmapai.test

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.roadmap.business.service.RoadmapService
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap


@RestController
@RequestMapping("/ai")
class TestController(
    private val rmService: RoadmapService
) {

    @GetMapping("/roadmap")
    fun getRoadmap(learningTarget: String): Mono<out Roadmap> {
        return rmService.requestRoadmap(learningTarget)
    }

}