package ua.torchers.roadmapai.ai.ai.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.theokanning.openai.client.OpenAiApi
import com.theokanning.openai.service.OpenAiService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import ua.torchers.roadmapai.ai.ai.*
import ua.torchers.roadmapai.ai.ai.model.AiService
import ua.torchers.roadmapai.ai.ai.model.AiServiceConfig
import ua.torchers.roadmapai.ai.ai.model.AiServiceEvent
import ua.torchers.roadmapai.shared.EitherError
import java.time.Duration
import java.util.concurrent.CopyOnWriteArrayList

@Service
class AiServicesContainer(basicConfig: OnStartAiServicesConfig, private val eventPublisher: ApplicationEventPublisher) {
    private val aiServices: MutableList<AiService> = CopyOnWriteArrayList()

    init {
        val builtServices = buildList {
            for (description in basicConfig.aiServices) {
                val service = buildService(description).getOrNull() ?: continue

                this@buildList.add(service)

                publishAiServiceAddedEvent(service)
            }
        }
        aiServices.addAll(builtServices)
    }

    private fun publishAiServiceAddedEvent(service: AiService) {
        val event = AiServiceEvent.ServiceAdded(this@AiServicesContainer, service)
        eventPublisher.publishEvent(event)
    }

    private fun buildEndpoint(description: AiServiceConfig.EndpointConfig): EitherError<AiService.Endpoint> =
        Either.catch {
            val objectMapper = OpenAiService.defaultObjectMapper()
            val httpClient =
                OpenAiService.defaultClient(description.token, Duration.ofMinutes(description.timeoutInMin))

            val endpoint = description.endpoint

            val retrofit = Retrofit.Builder()
                .client(httpClient)
                .baseUrl(endpoint)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build()

            AiService.Endpoint(endpoint, retrofit.create(OpenAiApi::class.java))
        }.mapLeft { EndpointBuildingException(it) }

    private fun buildService(description: AiServiceConfig): EitherError<AiService> {
        if (description.endpoints.isEmpty())
            return NoEndpointsException(description.name).left()

        val endpoints = buildList {
            for (endpoint in description.endpoints) {

                buildEndpoint(endpoint)
                    .fold(
                        {
                            logger.error("Failed to build endpoint $endpoint")
                            logger.error(it.message)
                        },
                        { builtEndpoint ->
                            add(builtEndpoint)
                        })
            }
        }

        if (endpoints.isEmpty())
            return NoEndpointsException(description.name).left()

        return AiService(description, CopyOnWriteArrayList(endpoints)).right()
    }

    fun addService(description: AiServiceConfig): EitherError<AiService> {
        val serviceEitherError = buildService(description)

        val service = serviceEitherError.getOrNull() ?: return serviceEitherError

        aiServices.removeIf { it.equalsByName(service) }
        aiServices.add(service)
        publishAiServiceAddedEvent(service)

        return serviceEitherError
    }

    fun removeService(serviceName: String): EitherError<String> {
        val serviceIterator = aiServices.iterator()

        while (serviceIterator.hasNext()) {
            val service = serviceIterator.next()

            if (service.equalsByName(serviceName)) {
                val removeServiceEvent = AiServiceEvent.ServiceRemoved(this, service)

                eventPublisher.publishEvent(removeServiceEvent)
                serviceIterator.remove()

                return serviceName.right()
            }
        }

        return NoServiceException(serviceName).left()
    }

    fun removeServiceEndpoint(serviceName: String, endpointUrl: String): EitherError<String> {
        val service =
            aiServices.find { it.equalsByName(serviceName) } ?: return NoServiceException(serviceName).left()

        if (service.endpoints.size == 1)
            return Exception("Trying to remove last endpoint").left()

        val endpointIterator = service.endpoints.iterator()
        while (endpointIterator.hasNext()) {
            val endpoint = endpointIterator.next()

            if (endpoint.equalsByEndpoint(endpointUrl)) {
                val endpointRemovedEvent = AiServiceEvent.EndpointRemoved(this, service, endpoint)

                eventPublisher.publishEvent(endpointRemovedEvent)
                endpointIterator.remove()

                return endpointUrl.right()
            }
        }

        return NoEndpointException(endpointUrl).left()

    }

    fun addServiceEndpoint(
        serviceName: String,
        endpointDesc: AiServiceConfig.EndpointConfig
    ): EitherError<AiService.Endpoint> {
        val service =
            aiServices.find { it.equalsByName(serviceName) } ?: return NoServiceException(serviceName).left()

        val foundEndpoint = service.endpoints.find { it.equalsByEndpoint(endpointDesc.endpoint) }
        if (foundEndpoint != null)
            return EndpointAlreadyExistException(endpointDesc.endpoint, service).left()

        val buildEndpointEither = buildEndpoint(endpointDesc)
        val builtEndpoint = buildEndpointEither.getOrNull() ?: return buildEndpointEither

        service.endpoints.add(builtEndpoint)

        val endpointAddedEvent = AiServiceEvent.EndpointAdded(this, service, builtEndpoint)
        eventPublisher.publishEvent(endpointAddedEvent)

        return builtEndpoint.right()
    }

    fun getServiceByName(name: String): AiService? = aiServices.find { it.equalsByName(name) }

    fun listServices(): List<AiService> = aiServices

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}