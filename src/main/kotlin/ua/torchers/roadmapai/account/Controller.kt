package ua.torchers.roadmapai.account

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.account.model.Account
import ua.torchers.roadmapai.account.model.RegisterRequest
import ua.torchers.roadmapai.account.service.AccountService
import java.security.Principal

@RestController
@RequestMapping("/account")
class Controller(
    private val accountService: AccountService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): Mono<Account> {
        return accountService.register(request)
    }

    @GetMapping("/get_me")
    fun getMe(principal: Principal): Mono<Account> {
        return accountService.accountByPrincipal(principal)
    }
}