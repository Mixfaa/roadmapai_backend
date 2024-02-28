package ua.torchers.roadmapai

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.config.SecurityConfig
import ua.torchers.roadmapai.roadmap.business.model.RoadmapCached

@SpringBootApplication
@Configuration
@EnableScheduling
@EnableWebFluxSecurity
@EnableWebFlux
@EnableReactiveMethodSecurity
@EnableReactiveMongoRepositories
@EnableConfigurationProperties
@ConfigurationPropertiesScan(
    "ua.torchers.roadmapai.ai.ai",
    "ua.torchers.roadmapai.ai.prompt"
)
@Import(SecurityConfig::class)
class RoadmapaiApplication {
    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        return JedisConnectionFactory()
    }

    @Bean
    fun redisTemplate(connectionFactory: JedisConnectionFactory): RedisTemplate<String, RoadmapCached> {
        val template = RedisTemplate<String, RoadmapCached>()
        template.connectionFactory = connectionFactory
        template.valueSerializer = GenericToStringSerializer(RoadmapCached::class.java)
        return template
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

fun main(args: Array<String>) {
    runApplication<RoadmapaiApplication>(*args)
}
