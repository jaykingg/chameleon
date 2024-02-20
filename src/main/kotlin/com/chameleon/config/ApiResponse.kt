package com.chameleon.config

data class ApiResponse<T>(
    val meta: Meta,
    val data: T?
) {
    data class Meta(
        val code: Int,
        val message: String
    )
}