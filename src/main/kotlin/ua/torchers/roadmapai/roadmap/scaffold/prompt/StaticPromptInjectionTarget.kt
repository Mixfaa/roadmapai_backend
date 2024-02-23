package ua.torchers.roadmapai.roadmap.scaffold.prompt

import ua.torchers.roadmapai.ai.prompt.model.PromptDescription

abstract class StaticPromptInjectionTarget(override val targetPromptName: String) : WrappedPrompt {
    override lateinit var prompt: PromptDescription
}