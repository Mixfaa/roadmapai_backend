package ua.torchers.roadmapai.ai.prompt.service

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import ua.torchers.roadmapai.ai.prompt.OnStartPromptsConfig
import ua.torchers.roadmapai.ai.prompt.model.PromptDescription
import ua.torchers.roadmapai.ai.prompt.model.PromptUpdatedEvent
import java.util.concurrent.CopyOnWriteArrayList

@Component
class PromptsManager(
    basicConfig: OnStartPromptsConfig,
    private val eventPublisher: ApplicationEventPublisher
) {
    private val promptsList: MutableList<PromptDescription> = CopyOnWriteArrayList()

    init {
        for (promptDescription in basicConfig.prompts) {
            eventPublisher.publishEvent(PromptUpdatedEvent(this, promptDescription))
            logger.info("New prompt: ${promptDescription.name}")
        }

        promptsList.addAll(basicConfig.prompts)
    }

    fun listPrompts(): List<PromptDescription> {
        return promptsList
    }

    fun getPromptByName(name: String): PromptDescription? {
        return promptsList.find { it.equalsByName(name) }
    }

    @PreAuthorize("hasRole('ADMIN')")
    fun patchPrompt(newPrompt: PromptDescription) {
        eventPublisher.publishEvent(PromptUpdatedEvent(this, newPrompt))

        promptsList.removeIf { it.equalsByName(newPrompt) }
        promptsList.add(newPrompt)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}