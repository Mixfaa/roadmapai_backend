package ua.torchers.roadmapai.ai.prompt.model

import org.springframework.context.ApplicationEvent

class PromptUpdatedEvent(
    src: Any,
    val promptDescription: PromptDescription
) : ApplicationEvent(src)