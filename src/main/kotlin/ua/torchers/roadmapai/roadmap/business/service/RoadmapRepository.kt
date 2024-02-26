package ua.torchers.roadmapai.roadmap.business.service

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import ua.torchers.roadmapai.roadmap.business.model.RoadmapEntity

@Repository
interface RoadmapRepository : ReactiveMongoRepository<RoadmapEntity, String>