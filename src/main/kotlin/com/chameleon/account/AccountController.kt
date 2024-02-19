package com.chameleon.account

import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class AccountController(private val accountRepository: AccountRepository, private val passwordEncoder: BCryptPasswordEncoder) {

    @PostMapping("/register")
    fun registerUser(@RequestBody account: Account): ResponseEntity<Any> {
        account.password = passwordEncoder.encode(account.password)
        accountRepository.save(account)
        return ResponseEntity.ok().body(mapOf("message" to "유저 등록 성공"))
    }
}