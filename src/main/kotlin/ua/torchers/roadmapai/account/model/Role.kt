package ua.torchers.roadmapai.account.model

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Role(
    permissions: Set<Permission>
) {
    ADMIN(setOf(Permission.ADMIN_PERMISSION)),
    USER(setOf(Permission.USER_PERMISSION));

    val grantedAuthorities: MutableList<SimpleGrantedAuthority> =
        permissions.mapTo(mutableListOf()) { SimpleGrantedAuthority(it.name) }
            .also { it.add(SimpleGrantedAuthority("ROLE_${this.name}")) }
}