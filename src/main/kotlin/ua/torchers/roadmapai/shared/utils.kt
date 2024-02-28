package ua.torchers.roadmapai.shared

import arrow.core.Either
import arrow.core.getOrElse
import com.theokanning.openai.completion.chat.ChatCompletionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable

typealias EitherError<T> = Either<Throwable, T>

inline fun <R> runOrNull(block: () -> R): R? {
    return try {
        block()
    } catch (ex: Exception) {
        null
    }
}

const val MAX_PAGE_SIZE = 15

fun Pageable.isNotInBound(maxPageSize: Int = MAX_PAGE_SIZE) : Boolean {
    return this.pageSize !in 1..maxPageSize
}

fun ChatCompletionResult.firstChoiceText(): String? = this.choices.firstOrNull()?.message?.content