package ua.torchers.roadmapai.roadmap

import ua.torchers.roadmapai.ai.ai.model.AiService

class UnclearAiAnswerException(msg: String) : Exception(msg)
class NoCachedValue(msg: String) : Exception(msg)
class NoChoicesFromAi(model: AiService) : Exception("Model $model returned zero choices")
