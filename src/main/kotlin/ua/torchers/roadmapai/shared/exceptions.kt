package ua.torchers.roadmapai.shared

import org.springframework.data.domain.Pageable

class NotFoundException(subject: String) : Exception("$subject: not found")

class LargePageSizeException(pageable: Pageable, maxPageSize: Int = MAX_PAGE_SIZE) :
    Exception("Page size ${pageable.pageSize} is too big, page size should be <= $maxPageSize")