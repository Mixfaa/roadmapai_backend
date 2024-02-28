package ua.torchers.roadmapai.shared

import arrow.core.Either
import com.theokanning.openai.completion.chat.ChatCompletionResult
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap

typealias EitherError<T> = Either<Throwable, T>

inline fun <R> runOrNull(block: () -> R): R? {
    return try {
        block()
    } catch (ex: Exception) {
        null
    }
}

const val MAX_PAGE_SIZE = 15

fun Pageable.isNotInBound(maxPageSize: Int = MAX_PAGE_SIZE): Boolean {
    return this.pageSize !in 1..maxPageSize
}

fun ChatCompletionResult.firstChoiceText(): String? = this.choices.firstOrNull()?.message?.content

private val roadmapNotFoundException = NotFoundException("Roadmap")
fun <T : Roadmap> Mono<T>.orRoadmapNotFound() = this.switchIfEmpty(Mono.error(roadmapNotFoundException))
fun <T : Roadmap> Flux<T>.orRoadmapNotFound() = this.switchIfEmpty(Flux.error(roadmapNotFoundException))

