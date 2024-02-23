package ua.torchers.roadmapai.roadmap.scaffold.prompt

import ua.torchers.roadmapai.ai.prompt.model.PromptDescription

interface WrappedPrompt {
    val targetPromptName: String
    var prompt: PromptDescription
}