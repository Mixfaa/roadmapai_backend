package ua.torchers.roadmapai.roadmap.business.model

import org.bson.types.ObjectId
import ua.torchers.roadmapai.account.model.Account
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap

data class RoadmapEntity(
    val id: ObjectId = ObjectId(),
    override val name: String,
    override val description: String,
    override val nodes: List<RmNodeEntity>,
    override val usedService: String,
    val owner: Account
) : Roadmap {
    constructor(roadmap: Roadmap, owner: Account) : this(
        ObjectId(),
        roadmap.name,
        roadmap.description,
        roadmap.nodes.map(::RmNodeEntity),
        roadmap.usedService,
        owner
    )

    data class RmNodeEntity(
        val id: ObjectId = ObjectId(),
        override val description: String,
        override val content: RmContentEntity?
    ) : Roadmap.RmNode {
        constructor(entity: Roadmap.RmNode) : this(
            ObjectId(),
            entity.description,
            entity.content?.let(::RmContentEntity)
        )
    }

    data class RmContentEntity(
        val id: ObjectId = ObjectId(),
        override val content: String
    ) : Roadmap.RmContent {
        constructor(entity: Roadmap.RmContent) : this(ObjectId(), entity.content)
    }
}