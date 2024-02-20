package com.chameleon.account

import jakarta.validation.constraints.NotBlank

data class LoginPayload(
    @field: NotBlank
    val mobileNumber: String,

    @field: NotBlank
    val password: String
)