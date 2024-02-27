package ua.torchers.roadmapai.roadmap

import ua.torchers.roadmapai.account.model.Account
import ua.torchers.roadmapai.ai.ai.model.AiService

class UnclearAiAnswerException(msg: String) : Exception(msg)
class NoCachedValue(msg: String) : Exception(msg)
class NoChoicesFromAi(model: AiService) : Exception("Model $model returned zero choices")
class AccessException(account: Account, obj: Any) : Exception("${account.username} can`t access $obj")