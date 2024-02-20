package com.chameleon.account

import com.chameleon.config.ApiResponse
import com.chameleon.security.JwtUtil
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authenticationManager: AuthenticationManager, private val jwtUtil: JwtUtil) {

    /**
     * 로그인 API
     * 성공시 JWT 토큰 리턴, 10시간 유효
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody
        payload: LoginPayload
    ): ResponseEntity<ApiResponse<String>> {
        return try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(payload.mobileNumber, payload.password)
            )
            val token = jwtUtil.generateToken(payload.mobileNumber)
            ResponseEntity.ok(
                ApiResponse(
                    meta = ApiResponse.Meta(code = HttpStatus.OK.value(), message = "로그인 성공."),
                    data = token
                )
            )
        } catch (e: AuthenticationException) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    meta = ApiResponse.Meta(code = HttpStatus.BAD_REQUEST.value(), message = "로그인 정보가 잘못되었습니다."),
                    data = null
                )
            )
        }
    }

    /**
     * 로그아웃 API
     * 별도로 개발하지 않은 이유
     * JWT 는 기본적으로 Stateless 함.
     * JWT 기반 인증의 경우 로그아웃 기능은 일반적으로 JWT 토큰을 삭제하여 클라이언트 측에서 처리함.
     * 만약 구현을 해야한다면 Redis cache 를 활용하여 미들레벨의 관리할 수 있음.
     */
}