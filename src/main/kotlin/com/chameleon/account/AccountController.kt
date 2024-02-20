package com.chameleon.account

import com.chameleon.config.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class AccountController(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    @PostMapping("/register")
    fun registerUser(
        @Valid @RequestBody
        accountPayload: AccountPayload
    ): ResponseEntity<ApiResponse<String>> {
        val encodedPassword = passwordEncoder.encode(accountPayload.password)
        accountRepository.save(
            Account(
                mobileNumber = accountPayload.mobileNumber,
                password = encodedPassword
            )
        )
        return ResponseEntity.ok(
            ApiResponse(
                meta = ApiResponse.Meta(code = HttpStatus.OK.value(), message = "사장님 등록이 완료되었습니다."),
                data = null
            )
        )
    }
}