package ua.torchers.roadmapai.roadmap.scaffold.model

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