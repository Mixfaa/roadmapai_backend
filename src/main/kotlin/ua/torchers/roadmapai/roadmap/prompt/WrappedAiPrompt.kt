package ua.torchers.roadmapai.roadmap.prompt

import ua.torchers.roadmapai.ai.prompt.model.PromptDescription

interface WrappedAiPrompt {
    val targetPromptName: String
    var prompt: PromptDescription
}

