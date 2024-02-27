package ua.torchers.roadmapai.account.service

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.account.UsernameTakenException
import ua.torchers.roadmapai.account.model.Account
import ua.torchers.roadmapai.account.model.RegisterRequest
import ua.torchers.roadmapai.account.model.Role
import ua.torchers.roadmapai.shared.runOrNull
import java.security.Principal

@Service
class AccountService(
    private val accountRepo: AccountRepo,
    private val passwordEncoder: PasswordEncoder
) : ReactiveUserDetailsService {

    fun accountByPrincipal(principal: Principal): Mono<Account> {
        return accountRepo.findById(principal.name)
    }

    fun register(request: RegisterRequest): Mono<Account> {
        return accountRepo.existsById(request.username)
            .flatMap { exists ->
                return@flatMap if (exists) Mono.error(UsernameTakenException(request.username))
                else accountRepo.save(
                    Account(
                        username = request.username,
                        password = passwordEncoder.encode(request.password),
                        role = runOrNull { Role.valueOf(request.role) } ?: Role.USER
                    )
                )
            }

    }

    override fun findByUsername(username: String): Mono<UserDetails> {
        return accountRepo.findById(username).cast(UserDetails::class.java)
    }
}