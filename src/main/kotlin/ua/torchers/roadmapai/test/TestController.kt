package ua.torchers.roadmapai.test

import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.torchers.roadmapai.ai.ai.service.AiRequestExecutionService
import ua.torchers.roadmapai.ai.ai.service.AiServicesContainer
import ua.torchers.roadmapai.roadmap.model.RoadmapDto
import ua.torchers.roadmapai.roadmap.prompt.wrapped.*

private const val RESPONSE =
    "```json\n{\n    \"name\": \"Learning Plan for Cooking Eggs\",\n    \"description\": \"A roadmap for developing cooking skills in egg preparation, including basic and advanced techniques.\",\n    \"nodes\": [\n        {\n            \"name\": \"Understand the basics of cooking eggs (level 1)\",\n            \"content\": [\n                {\n                    \"type\": \"lesson\",\n                    \"title\": \"Learn about different types of eggs and their uses\",\n                    \"description\": \"Learn about the different types of eggs, such as chicken, duck, and quail, and their various uses in cooking.\"\n                },\n                {\n                    \"type\": \"lesson\",\n                    \"title\": \"Basic techniques for boiling, frying, poaching, and baking eggs\",\n                    \"description\": \"Learn the basic techniques for cooking eggs, including boiling, frying, poaching, and baking.\"\n                },\n                {\n                    \"type\": \"lesson\",\n                    \"title\": \"Develop basic skills in cooking eggs (level 2)\",\n                    \"description\": \"Practice cooking eggs to achieve consistent doneness and learn how to fry them to different levels of doneness.\"\n                },\n                {\n                    \"type\": \"lesson\",\n                    \"title\": \"Master advanced techniques in cooking eggs (level 3)\",\n                    \"description\": \"Learn how to make omelets and egg dishes with multiple ingredients, and practice making custard and other egg-based desserts.\"\n                },\n                {\n                    \"type\": \"lesson\",\n                    \"title\": \"Experiment with new egg recipes (level 4)\",\n                    \"description\": \"Try new and exotic egg varieties, such as quail or pheasant eggs, and experiment with different marinades and seasonings to add unique flavors to your eggs.\"\n                }\n            ]\n        }\n    }\n```\nNote: The schema above is a simplified example, and the actual response may vary depending on the complexity of the data."


@RestController
@RequestMapping("/ai")
class TestController(
    private val aiServicesContainer: AiServicesContainer,
    private val aiRequestExecutionService: AiRequestExecutionService,
) {

    @GetMapping("/roadmap")
    fun getRoadmap(learningTarget: String): RoadmapDto? {

        BuildRoadmap.makeRequest("hello")
        ChooseLangModel.makeRequest("hello", listOf())
        ConvertToJson.makeRequest("hello", "hello")
        RemoveExtra.makeRequest("hello")
        RoadmapRelevanceCheck.makeRequest(RoadmapDto(ObjectId(), "hello", "hello", listOf()))

        return null
    }

}