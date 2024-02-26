package ua.torchers.roadmapai.account.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.account.UsernameTakenException
import ua.torchers.roadmapai.account.model.Account
import ua.torchers.roadmapai.account.model.RegisterRequest

@Service
class AccountService(
    private val accountRepo: AccountRepo
) : UserDetailsService {

    fun register(request: RegisterRequest): Mono<Account> {
        return accountRepo.existsById(request.username)
            .flatMap { exists ->
                if (exists) return@flatMap Mono.error(UsernameTakenException(request.username))
                return@flatMap accountRepo.save(Account(request.username, request.username, request.authorities))
            }

    }

    override fun loadUserByUsername(username: String): UserDetails {
        return accountRepo.findById(username)
            .block() ?: throw UsernameNotFoundException("User with $username not found")
    }
}