package ua.torchers.roadmapai.account.model

data class RegisterRequest(
    val username: String,
    val password: String,
    val role: String
)