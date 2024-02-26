package ua.torchers.roadmapai.account.model

import org.springframework.security.core.GrantedAuthority

enum class Role() : GrantedAuthority {
    USER,
    ADMIN;

    override fun getAuthority(): String {
        return this.name
    }
}