package ua.torchers.roadmapai

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableConfigurationProperties
@ConfigurationPropertiesScan(
    "ua.torchers.roadmapai.ai.ai",
    "ua.torchers.roadmapai.ai.prompt"
)
class RoadmapaiApplication

fun main(args: Array<String>) {
    runApplication<RoadmapaiApplication>(*args)
}
