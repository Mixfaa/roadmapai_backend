package ua.torchers.roadmapai.roadmap.business.service

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import ua.torchers.roadmapai.roadmap.business.model.RoadmapEntity

interface RoadmapRepo : ReactiveMongoRepository<RoadmapEntity, String> {

}