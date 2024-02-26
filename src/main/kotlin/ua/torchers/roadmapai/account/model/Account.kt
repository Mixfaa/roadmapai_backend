package ua.torchers.roadmapai.account.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Document("account")
data class Account(
    @JvmField @Id val username: String,
    @JvmField @field:JsonIgnore val password: String,
    @JvmField val authorities: MutableCollection<out GrantedAuthority>
) : UserDetails {
    constructor(username: String, password: String, authority: GrantedAuthority) : this(username,password, mutableListOf(authority))

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities
    override fun getPassword(): String = password
    override fun getUsername(): String = username
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

}