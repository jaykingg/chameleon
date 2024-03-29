package com.chameleon.account

import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Long> {
    fun findByMobileNumber(mobileNumber: String): Account?
}