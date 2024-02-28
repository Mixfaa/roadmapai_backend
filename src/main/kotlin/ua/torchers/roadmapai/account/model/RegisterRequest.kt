package ua.torchers.roadmapai.account.model

import ua.torchers.roadmapai.shared.NotBlankOrNull

data class RegisterRequest(
    @field:NotBlankOrNull val username: String,
    @field:NotBlankOrNull val password: String,
    @field:NotBlankOrNull val role: String
)
