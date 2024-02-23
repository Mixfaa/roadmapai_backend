package ua.torchers.roadmapai.roadmap.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId

open class RoadmapDto(
    val name: String,
    val description: String,
    val nodes: List<RmNodeDto>,
) {
    open class RmNodeDto(
        val name: String,
        val content: RmContentDto? = null,
    )

    open class RmContentDto(
        val content: String,
    )

    fun stringForAi(): String {
        return buildString {
            append("Roadmap: ").appendLine(name)
            append("Description: ").appendLine(description)

            for (node in nodes) append(" * ").appendLine(node.name)
            appendLine("Roadmap end")
            appendLine()
        }
    }

    companion object {
        @JsonIgnore
        const val JSON_SCHEMA: String = """
            Roadmap json schema:
            {
                "name" // string
                "description" // string
                "nodes" // array of RmNode
            }
            
            RmNode json schema:
            {
                "name" // string
                "content" // String (optionally)
            }
        """
    }
}

class Roadmap(
    val id: ObjectId = ObjectId(), name: String, description: String, nodes: List<RmNode>
) : RoadmapDto(name, description, nodes) {
    class RmNode(
        val id: ObjectId = ObjectId(), name: String, content: RmContent?
    ) : RmNodeDto(name, content)

    class RmContent(val id: ObjectId = ObjectId(), content: String) : RmContentDto(content)
}
