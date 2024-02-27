package ua.torchers.roadmapai.roadmap.scaffold.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId

interface Roadmap {
    val name: String
    val description: String
    val nodes: List<RmNode>
    val usedService: String

    interface RmNode {
        val description: String
        val content: RmContent?
    }

    interface RmContent {
        val content: String
    }

    fun stringForAi(): String {
        return buildString {
            append("Roadmap: ").appendLine(name)
            append("Description: ").appendLine(description)

            for (node in nodes) append(" * ").appendLine(node.description)
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
                "description" // string
                "content" // String (optionally)
            }
        """
    }
}

data class RoadmapDto(
    override val name: String,
    override val description: String,
    override val nodes: List<RmNodeDto>,
    override val usedService: String
) : Roadmap {

    data class RmNodeDto(
        override val description: String,
        override val content: RmContentDto? = null,
    ) : Roadmap.RmNode

    data class RmContentDto(
        override val content: String,
    ) : Roadmap.RmContent
}
