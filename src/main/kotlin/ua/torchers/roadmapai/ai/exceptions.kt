package ua.torchers.roadmapai.ai

import ua.torchers.roadmapai.ai.ai.model.AiService

class EndpointAlreadyExistException(endpoint: String, aiService: AiService) :
    Exception("Endpoint $endpoint already exist in service ${aiService.modelId}")

class NoServiceException(serviceName: String) : Exception("No service with name $serviceName")

class NoEndpointsException(serviceName: String) : Exception("No endpoints for $serviceName")

class NoEndpointException(endpoint: String) : Exception("No endpoints with url  $endpoint")

class EndpointBuildingException(cause: Throwable) : Exception(cause)

class UnclearAiAnswerException(msg: String) : Exception(msg)

class NoChoicesFromAi(model: AiService) : Exception("Model $model returned empty choices")
