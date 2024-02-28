package ua.torchers.roadmapai.roadmap

import org.springframework.data.domain.Pageable
import ua.torchers.roadmapai.account.model.Account
import ua.torchers.roadmapai.ai.ai.model.AiService
import ua.torchers.roadmapai.shared.MAX_PAGE_SIZE

class UnclearAiAnswerException(msg: String) : Exception(msg)

class NoCachedValue(msg: String) : Exception(msg)

class NoChoicesFromAi(model: AiService) : Exception("Model $model returned empty choices")

class AccessException(account: Account, obj: Any) : Exception("${account.username} can`t access $obj")

class LargePageSizeException(pageable: Pageable, maxPageSize: Int = MAX_PAGE_SIZE) :
    Exception("Page size ${pageable.pageSize} is too big, page size should be <= $maxPageSize")