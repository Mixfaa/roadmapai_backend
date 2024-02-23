package ua.torchers.roadmapai.roadmap.service

import org.reflections.Reflections
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import ua.torchers.roadmapai.ai.prompt.model.PromptUpdatedEvent
import ua.torchers.roadmapai.roadmap.prompt.StaticAiPromptInjectionTarget

@Service
class StaticAiPromptInjector : ApplicationListener<PromptUpdatedEvent> {
    private val wrappedPromptObjects = Reflections(StaticAiPromptInjectionTarget::class.java.`package`.name)
        .getSubTypesOf(StaticAiPromptInjectionTarget::class.java)
        .mapNotNull { it.kotlin.objectInstance }


    override fun onApplicationEvent(event: PromptUpdatedEvent) {
        for (wrappedPromptObject in wrappedPromptObjects)
            if (wrappedPromptObject.targetPromptName == event.promptDescription.name)
                wrappedPromptObject.prompt = event.promptDescription
    }
}