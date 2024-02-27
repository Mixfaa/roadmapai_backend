package ua.torchers.roadmapai.shared

import arrow.core.Either
import arrow.core.getOrElse
import com.theokanning.openai.completion.chat.ChatCompletionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

typealias EitherError<T> = Either<Throwable, T>

inline fun <R> runOrNull(block: () -> R): R? {
    return try {
        block()
    } catch (ex: Exception) {
        null
    }
}

fun <T> EitherError<T>.getOrThrow(): T {
    return this.getOrElse { throw it }
}

private val LOGGER = LoggerFactory.getLogger("Utils logger")

fun Throwable.logError(logger: Logger) {
    logger.error(this.message)
}

fun Throwable.logError(src: Any) {
    LOGGER.error("{} Error: {}", src, this.message)
}

fun ChatCompletionResult.firstChoiceText(): String? = this.choices.firstOrNull()?.message?.content