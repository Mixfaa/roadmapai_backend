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
    val role: Role
) : UserDetails {
    override fun getUsername(): String = username

    @JsonIgnore
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = role.grantedAuthorities
    @JsonIgnore
    override fun getPassword(): String = password
    @JsonIgnore
    override fun isAccountNonExpired(): Boolean = true
    @JsonIgnore
    override fun isAccountNonLocked(): Boolean = true

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean = true

    @JsonIgnore
    override fun isEnabled(): Boolean = true

}