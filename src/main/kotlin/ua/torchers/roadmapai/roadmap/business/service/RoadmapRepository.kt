package ua.torchers.roadmapai.roadmap.business.service

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import ua.torchers.roadmapai.account.model.Account
import ua.torchers.roadmapai.roadmap.business.model.RoadmapEntity

@Repository
interface RoadmapRepository : ReactiveMongoRepository<RoadmapEntity, String> {
    fun findByOwner(owner: Account, pageable: Pageable): Flux<RoadmapEntity>
}