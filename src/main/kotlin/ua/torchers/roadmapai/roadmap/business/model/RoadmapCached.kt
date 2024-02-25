package ua.torchers.roadmapai.roadmap.business.model

import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap
import ua.torchers.roadmapai.roadmap.scaffold.model.RoadmapDto
import java.util.*

data class RoadmapCached(
    override val name: String,
    override val description: String,
    override val nodes: List<RoadmapDto.RmNodeDto>,
    override val usedService: String,
    val id: String,
) : Roadmap {
    constructor(dto: RoadmapDto, id: UUID) : this(dto.name, dto.description, dto.nodes, dto.usedService, id.toString())
}