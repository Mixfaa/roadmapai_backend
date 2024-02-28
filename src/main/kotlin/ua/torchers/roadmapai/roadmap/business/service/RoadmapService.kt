package ua.torchers.roadmapai.roadmap.business.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.account.AccessException
import ua.torchers.roadmapai.account.model.Account
import ua.torchers.roadmapai.roadmap.RoadmapContentNotGenerated
import ua.torchers.roadmapai.roadmap.business.model.RoadmapCached
import ua.torchers.roadmapai.roadmap.business.model.RoadmapEntity
import ua.torchers.roadmapai.roadmap.orRoadmapNotFound
import ua.torchers.roadmapai.roadmap.roadmapNotFoundException
import ua.torchers.roadmapai.roadmap.scaffold.model.RoadmapDto
import ua.torchers.roadmapai.roadmap.scaffold.model.TestDto
import ua.torchers.roadmapai.roadmap.scaffold.service.RoadmapCreationService
import ua.torchers.roadmapai.roadmap.scaffold.service.TestCreationService
import ua.torchers.roadmapai.shared.LargePageSizeException
import ua.torchers.roadmapai.shared.NotFoundException
import ua.torchers.roadmapai.shared.isNotInBound
import java.time.Duration
import java.util.*

/**
 * Or simply facade
 */
@Service
class RoadmapService(
    @Value("\${roadmap.cache.expiration}") private val expirationInMinutes: Long,
    private val roadmapRepo: RoadmapRepository,
    private val roadmapCache: RedisTemplate<String, RoadmapCached>,
    private val rmCreationService: RoadmapCreationService,
    private val testCreationService: TestCreationService
) {
    private val cacheExpiration = Duration.ofMinutes(expirationInMinutes)

    @PreAuthorize("#requester.username == authentication.name")
    fun deleteRoadmap(id: String, requester: Account): Mono<Void> {
        return roadmapRepo.findById(id)
            .orRoadmapNotFound()
            .flatMap {
                if (it.owner != requester) Mono.error(AccessException(requester, it))
                else roadmapRepo.delete(it)
            }
    }

    @PreAuthorize("#requester.username == authentication.name")
    fun saveFromCache(id: String, requester: Account): Mono<RoadmapEntity> {
        val roadmapCached = roadmapCache.opsForList().leftPop(id)
            ?: return Mono.error(roadmapNotFoundException)

        return roadmapRepo.save(RoadmapEntity(roadmapCached, requester))
    }

    @PreAuthorize("#requester.username == authentication.name")
    fun getRoadmap(id: String, requester: Account): Mono<RoadmapEntity> {
        return roadmapRepo
            .findById(id)
            .orRoadmapNotFound()
            .flatMap {
                if (it.owner != requester) Mono.error(AccessException(requester, it))
                else Mono.just(it)
            }
    }

    @PreAuthorize("#requester.username == authentication.name")
    fun getMyRoadmaps(requester: Account, pageable: Pageable): Flux<RoadmapEntity> {
        if (pageable.isNotInBound()) return Flux.error(LargePageSizeException(pageable))
        return roadmapRepo.findByOwner(requester, pageable).orRoadmapNotFound()
    }

    fun searchForRoadmaps(query: String, pageable: Pageable): Flux<RoadmapEntity> {
        if (pageable.isNotInBound()) return Flux.error(LargePageSizeException(pageable))
        return roadmapRepo.findAllByText(query, pageable).orRoadmapNotFound()
    }

    private fun saveToCache(roadmap: RoadmapDto): RoadmapCached {
        val uniqueId = UUID.randomUUID()
        val roadmapCached = RoadmapCached(roadmap, uniqueId)

        roadmapCache.opsForList().leftPush(roadmapCached.id, roadmapCached)
        roadmapCache.opsForList().operations.expire(roadmapCached.id, cacheExpiration)
        return roadmapCached
    }

    fun requestRoadmap(learningTarget: String): Mono<RoadmapCached> {
        return rmCreationService.createRoadmap(learningTarget).map(::saveToCache)
    }

    fun requestTest(roadmapId: String, nodeId: String): Mono<TestDto> {
        return roadmapRepo.findById(roadmapId)
            .orRoadmapNotFound()
            .flatMap { roadmap ->
                val node = roadmap.nodes.find { it.id.toHexString() == nodeId }
                    ?: return@flatMap Mono.error(NotFoundException("Node $nodeId not found in roadmap ($roadmapId)"))

                val content = node.content
                    ?: return@flatMap Mono.error(RoadmapContentNotGenerated(roadmap, node))

                testCreationService.generateTestFor(roadmap, content)
            }
    }
}