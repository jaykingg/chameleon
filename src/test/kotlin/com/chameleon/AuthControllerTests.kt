package com.chameleon

import com.chameleon.account.LoginPayload
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests(
    private val mockMvc: MockMvc
) : BehaviorSpec({
    val endpoint = "/api/auth"
    val objectMapper = jacksonObjectMapper()

    Given("인증") {
        When("사용자가 없는 경우") {
            val payload = LoginPayload(
                mobileNumber = "010-4321-4321",
                password = "1234"
            )
            Then("Response 400") {
                mockMvc.perform(
                    MockMvcRequestBuilders.post("$endpoint/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            }
        }

        When("비밀번호가 잘못된 경우") {
            val payload = LoginPayload(
                mobileNumber = "010-1234-1234",
                password = "4321"
            )
            Then("Response 400") {
                mockMvc.perform(
                    MockMvcRequestBuilders.post("$endpoint/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            }
        }

        When("성공") {
            val payload = LoginPayload(
                mobileNumber = "010-1234-1234",
                password = "1234"
            )
            Then("Response 200, data 에 토큰 반환") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("$endpoint/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                )
                    .andExpect(status().isOk)
                    .andReturn()


                val response = objectMapper.readTree(result.response.contentAsString)
                response.get("data").shouldNotBeNull()
            }
        }
    }
})