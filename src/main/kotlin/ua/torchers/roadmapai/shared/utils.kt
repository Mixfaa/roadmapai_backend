package ua.torchers.roadmapai.shared

import arrow.core.Either
import org.slf4j.Logger
import org.slf4j.LoggerFactory

typealias EitherError<T> = Either<Throwable, T>

private val LOGGER = LoggerFactory.getLogger("Utils logger")

fun Throwable.logError(logger: Logger) {
    logger.error(this.message)
}

fun Throwable.logError(src: Any) {
    LOGGER.error("{} Error: {}", src, this.message)
}