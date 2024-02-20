package com.chameleon.account

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AccountPayload(
    @field: NotBlank
    @field: Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "핸드폰 번호의 양식과 맞지 않습니다")
    val mobileNumber: String,

    @field: NotBlank
    val password: String
)