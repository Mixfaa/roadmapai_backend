package ua.torchers.roadmapai.roadmap.prompt

import ua.torchers.roadmapai.ai.prompt.model.PromptDescription

abstract class StaticAiPromptInjectionTarget(override val targetPromptName: String) : WrappedAiPrompt {
    override lateinit var prompt: PromptDescription
}