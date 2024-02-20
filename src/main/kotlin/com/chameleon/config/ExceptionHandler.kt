package com.chameleon.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Any>> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val apiResponse = ApiResponse<Any>(
            meta = ApiResponse.Meta(code = status.value(), message = e.message ?: "예상치 못한 에러가 발생했습니다."),
            data = null
        )
        return ResponseEntity(apiResponse, status)
    }
}