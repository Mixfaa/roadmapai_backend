package ua.torchers.roadmapai.account.model

import org.springframework.security.core.GrantedAuthority
import javax.print.attribute.standard.RequestingUserName

data class RegisterRequest(
    val username: String,
    val password:String,
    val authorities: MutableCollection<out GrantedAuthority>
)