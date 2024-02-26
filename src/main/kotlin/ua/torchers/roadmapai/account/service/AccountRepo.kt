package ua.torchers.roadmapai.account.service

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import ua.torchers.roadmapai.account.model.Account

@Repository
interface AccountRepo : ReactiveMongoRepository<Account, String>