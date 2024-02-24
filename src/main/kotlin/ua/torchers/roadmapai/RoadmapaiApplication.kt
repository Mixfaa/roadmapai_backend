package ua.torchers.roadmapai

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories


@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableConfigurationProperties
@ConfigurationPropertiesScan(
    "ua.torchers.roadmapai.ai.ai",
    "ua.torchers.roadmapai.ai.prompt"
)
class RoadmapaiApplication {
//    @Bean
//    fun jedisConnectionFactory(): JedisConnectionFactory {
//        return JedisConnectionFactory()
//    }

//    @Bean
//    fun redisTemplate(connectionFactory: JedisConnectionFactory): RedisTemplate<String, Any> {
//        val template = RedisTemplate<String, Any>()
//        template.connectionFactory = connectionFactory
//        template.valueSerializer = GenericToStringSerializer(Any::class.java)
//        return template
//    }
}

fun main(args: Array<String>) {
    runApplication<RoadmapaiApplication>(*args)
}
