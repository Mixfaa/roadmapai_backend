package ua.torchers.roadmapai.roadmap.service

import org.reflections.Reflections
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import ua.torchers.roadmapai.ai.prompt.model.PromptUpdatedEvent
import ua.torchers.roadmapai.roadmap.prompt.StaticPromptInjectionTarget

@Service
class StaticPromptInjector : ApplicationListener<PromptUpdatedEvent> {
    private val wrappedPromptObjects = Reflections(StaticPromptInjectionTarget::class.java.`package`.name)
        .getSubTypesOf(StaticPromptInjectionTarget::class.java)
        .mapNotNull { it.kotlin.objectInstance }


    override fun onApplicationEvent(event: PromptUpdatedEvent) {
        for (wrappedPromptObject in wrappedPromptObjects)
            if (wrappedPromptObject.targetPromptName == event.promptDescription.name)
                wrappedPromptObject.prompt = event.promptDescription
    }
}