package com.chameleon.security

import com.chameleon.account.AccountRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val accountRepository: AccountRepository) : UserDetailsService {
    override fun loadUserByUsername(mobileNumber: String): UserDetails {
        val user = accountRepository.findByMobileNumber(mobileNumber)
            ?: throw UsernameNotFoundException("사용자의 해당 휴대폰 번호를 찾을 수 없습니다 : $mobileNumber")

        return User.withUsername(user.mobileNumber)
            .password(user.password)
            .authorities(emptyList())
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }
}