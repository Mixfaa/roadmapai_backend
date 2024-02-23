package ua.torchers.roadmapai.account.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepo: AccountRepo
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return accountRepo.findById(username)
            .blockOptional()
            .orElseThrow { UsernameNotFoundException("User with $username not found") }
    }
}