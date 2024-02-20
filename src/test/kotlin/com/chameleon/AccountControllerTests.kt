package com.chameleon

import com.chameleon.account.AccountPayload
import com.chameleon.account.AccountRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTests(
    private val mockMvc: MockMvc,
    private val accoutRepository: AccountRepository
) : BehaviorSpec({
    val endpoint = "/api/users"
    val objectMapper = jacksonObjectMapper()

    Given("회원가입") {
        When("휴대폰 번호 양식이 일치하지 않는 경우") {
            val payload = AccountPayload(
                mobileNumber = "123123",
                password = "1234"
            )
            Then("Response Error") {
                mockMvc.perform(
                    MockMvcRequestBuilders.post("$endpoint/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                ).andExpect(status().is5xxServerError)

            }
        }
        When("형식이 정상적인 경우") {
            val payload = AccountPayload(
                mobileNumber = "010-1234-1234",
                password = "1234"
            )

            beforeEach {
                accoutRepository.deleteAll()
            }

            Then("Response 200") {
                mockMvc.perform(
                    MockMvcRequestBuilders.post("$endpoint/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                ).andExpect(status().isOk)
            }
        }
    }
})