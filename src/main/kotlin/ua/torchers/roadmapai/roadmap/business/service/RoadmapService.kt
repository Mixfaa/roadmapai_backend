package ua.torchers.roadmapai.roadmap.business.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.account.model.Account
import ua.torchers.roadmapai.account.service.AccountService
import ua.torchers.roadmapai.roadmap.NoCachedValue
import ua.torchers.roadmapai.roadmap.business.model.RoadmapCached
import ua.torchers.roadmapai.roadmap.business.model.RoadmapEntity
import ua.torchers.roadmapai.roadmap.scaffold.model.RoadmapDto
import ua.torchers.roadmapai.roadmap.scaffold.service.RoadmapCreationService
import java.util.*

@Service
class RoadmapService(
    private val roadmapRepo: RoadmapRepository,
    private val roadmapCache: RedisTemplate<String, RoadmapCached>,
//    private val accountService: AccountService,
    private val rmCreationService: RoadmapCreationService
) {
    @PreAuthorize("#requester.username == authentication.name")
    fun deleteRoadmap(id: String, requester: Account): Mono<Void> {
        return roadmapRepo.findById(id)
            .flatMap {
                if (it.owner != requester)
                    Mono.error(AccessDeniedException("$requester can`t access $it"))
                else
                    roadmapRepo.delete(it)
            }
    }

    @PreAuthorize("#requester.username == authentication.name")
    fun saveFromCache(id: String, requester: Account): Mono<RoadmapEntity> {
        val roadmapCached = roadmapCache.opsForList().leftPop(id)
            ?: return Mono.error(NoCachedValue("Can`t find cached roadmap with id $id"))

        return roadmapRepo.save(RoadmapEntity(roadmapCached, requester))
    }

    private fun saveToCache(roadmap: RoadmapDto): RoadmapCached {
        val uniqueId = UUID.randomUUID()
        val roadmapCached = RoadmapCached(roadmap, uniqueId)

        roadmapCache.opsForList().leftPush(roadmapCached.id, roadmapCached)
        return roadmapCached
    }


    fun requestRoadmap(learningTarget: String): Mono<RoadmapCached> {
        return rmCreationService
            .createRoadmap(learningTarget)
            .map(::saveToCache)
    }

}