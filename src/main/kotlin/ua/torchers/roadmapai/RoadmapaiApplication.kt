package ua.torchers.roadmapai

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import ua.torchers.roadmapai.roadmap.business.model.RoadmapCached


@SpringBootApplication
@EnableScheduling
@EnableReactiveMongoRepositories
@EnableConfigurationProperties
@EnableMethodSecurity
@ConfigurationPropertiesScan(
    "ua.torchers.roadmapai.ai.ai",
    "ua.torchers.roadmapai.ai.prompt"
)
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
}

fun main(args: Array<String>) {
    runApplication<RoadmapaiApplication>(*args)
}
