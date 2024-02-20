package com.chameleon

import com.chameleon.account.LoginPayload
import com.chameleon.product.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTests(
    private var mockMvc: MockMvc,
    private val productRepository: ProductRepository
) : BehaviorSpec({
    val endpoint = "/api/products"
    val objectMapper = jacksonObjectMapper()

    fun loginAndGetToken(): String {
        val loginPayload = LoginPayload(
            mobileNumber = "010-1234-1234",
            password = "1234"
        )

        val result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload))
        ).andExpect(status().isOk)
            .andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        return response.get("data").asText()
    }

    Given("상품 등록") {

        When("payload 가 유효하지 않은 경우") {
            val jwtToken = loginAndGetToken()
            val payload = ProductPayload(
                category = "커피",
                price = -1000,
                cost = -1,
                name = "라떼",
                description = "아주 맛있어요",
                barcode = "0123456789123",
                expirationDate = Date.from(Instant.now()),
                size = ProductSize.LARGE
            )
            Then("Response") {
                mockMvc.perform(
                    MockMvcRequestBuilders.post("$endpoint/register")
                        .header("Authorization", "Bearer $jwtToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                ).andExpect(MockMvcResultMatchers.status().isInternalServerError)
            }
        }

        When("payload 유효햔 경우") {
            val jwtToken = loginAndGetToken()
            val payload = ProductPayload(
                category = "커피",
                price = 5000,
                cost = 500,
                name = "라떼",
                description = "아주 맛있어요",
                barcode = "0123456789123",
                expirationDate = Date.from(Instant.now()),
                size = ProductSize.LARGE
            )
            Then("Response 200") {
                mockMvc.perform(
                    MockMvcRequestBuilders.post("$endpoint/register")
                        .header("Authorization", "Bearer $jwtToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                ).andExpect(status().isOk)
                    .andReturn()
            }
        }
    }

    Given("단일 상품 조회") {
        When("상품 아이디가 없는 경우") {
            val jwtToken = loginAndGetToken()
            val product = Product(
                id = 1,
                category = "커피",
                price = 5000,
                cost = 500,
                name = "라떼",
                description = "아주 맛있어요",
                barcode = "0123456789123",
                expirationDate = Date.from(Instant.now()),
                size = ProductSize.LARGE
            )

            beforeEach {
                productRepository.save(product)
            }

            afterEach {
                productRepository.deleteAll()
            }

            Then("Response 200, Null 응답(추후 확장가능성 고려)") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.get("$endpoint/2")
                        .header("Authorization", "Bearer $jwtToken")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()

                val response = objectMapper.readTree(result.response.contentAsString)
                response.get("data").isNull
            }
        }
        When("상품이 존재하는 경우") {
            val jwtToken = loginAndGetToken()
            val product = Product(
                id = 1,
                category = "커피",
                price = 5000,
                cost = 500,
                name = "라떼",
                description = "아주 맛있어요",
                barcode = "0123456789123",
                expirationDate = Date.from(Instant.now()),
                size = ProductSize.LARGE
            )

            beforeEach {
                productRepository.save(product)
            }

            afterEach {
                productRepository.deleteAll()
            }

            Then("Response 200, 조회 데이터 응답") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.get("$endpoint/${product.id}")
                        .header("Authorization", "Bearer $jwtToken")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()

                val response = objectMapper.readTree(result.response.contentAsString)
                response.get("data").get("id").should { product.id }
            }
        }
    }

    Given("다수 상품 조회") {
        When("상품들이 존재하는 경우") {
            val jwtToken = loginAndGetToken()
            beforeEach {
                repeat(20) {
                    val product = Product(
                        category = "커피",
                        price = 5000,
                        cost = 500,
                        name = "라떼",
                        description = "아주 맛있어요",
                        barcode = "0123456789123",
                        expirationDate = Date.from(Instant.now()),
                        size = ProductSize.LARGE
                    )
                    productRepository.save(product)
                }
                productRepository.flush()
            }

            afterEach {
                productRepository.deleteAll()
            }

            Then("Response, List 형태이며 사이즈는 10 이다") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.get("$endpoint/list")
                        .header("Authorization", "Bearer $jwtToken")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()

                val response = objectMapper.readTree(result.response.contentAsString)
                response.get("data").size() shouldBe 10
            }
        }
    }

    Given("상품 검색") {
        When("해당 검색어의 상품이 없는 경우") {
            val jwtToken = loginAndGetToken()
            beforeEach {
                productRepository.deleteAll()
            }

            Then("Response, 빈 리스트 반환") {
                mockMvc.perform(
                    MockMvcRequestBuilders.get("$endpoint/search")
                        .header("Authorization", "Bearer $jwtToken")
                        .param("name", "라떼")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                ).andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(jsonPath("$.data").isEmpty)


            }
        }
        When("해당 검색어의 상품이 있는 경우") {
            When("검색어로 검색한 경우") {
                val jwtToken = loginAndGetToken()
                val searchString = "슈크림 라떼"
                beforeEach {
                    productRepository.deleteAll()
                    productRepository.save(
                        Product(
                            category = "커피",
                            price = 5000,
                            cost = 500,
                            name = "슈크림 라떼",
                            description = "hello",
                            barcode = "0123456789123",
                            expirationDate = Date.from(Instant.now()),
                            size = ProductSize.LARGE
                        )
                    )
                }

                afterEach {
                    productRepository.deleteAll()
                }

                Then("Response 200, 조회된 결과 응답") {
                    mockMvc.perform(
                        MockMvcRequestBuilders.get("$endpoint/search")
                            .header("Authorization", "Bearer $jwtToken")
                            .param("name", "$searchString")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                    ).andExpect(status().isOk)
                        .andExpect(jsonPath("$.data[0].name").value("슈크림 라떼"))
                }
            }
            When("%LIKE로 검색한 경우") {
                val jwtToken = loginAndGetToken()
                val searchString = "슈크림"
                beforeEach {
                    productRepository.deleteAll()
                    productRepository.save(
                        Product(
                            category = "커피",
                            price = 5000,
                            cost = 500,
                            name = "슈크림 라떼",
                            description = "hello",
                            barcode = "0123456789123",
                            expirationDate = Date.from(Instant.now()),
                            size = ProductSize.LARGE
                        )
                    )
                }

                afterEach {
                    productRepository.deleteAll()
                }

                Then("Response 200, 조회된 결과 응답") {
                    mockMvc.perform(
                        MockMvcRequestBuilders.get("$endpoint/search")
                            .header("Authorization", "Bearer $jwtToken")
                            .param("name", "$searchString")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                    ).andExpect(status().isOk)
                        .andExpect(jsonPath("$.data[0].name").value("슈크림 라떼"))
                }
            }
            When("초성검색한 경우") {
                val jwtToken = loginAndGetToken()
                val searchString = "ㅅㅋㄹ"
                beforeEach {
                    productRepository.deleteAll()
                    productRepository.save(
                        Product(
                            category = "커피",
                            price = 5000,
                            cost = 500,
                            name = "슈크림 라떼",
                            description = "hello",
                            barcode = "0123456789123",
                            expirationDate = Date.from(Instant.now()),
                            size = ProductSize.LARGE
                        )
                    )
                }

                afterEach {
                    productRepository.deleteAll()
                }

                Then("Response 200, 조회된 결과 응답") {
                    mockMvc.perform(
                        MockMvcRequestBuilders.get("$endpoint/search")
                            .header("Authorization", "Bearer $jwtToken")
                            .param("name", "$searchString")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                    ).andExpect(status().isOk)
                        .andExpect(jsonPath("$.data[0].name").value("슈크림 라떼"))
                }
            }
        }
    }

    Given("상품 수정") {
        When("payload 가 유효하지 않은 경우") {
            val jwtToken = loginAndGetToken()
            Then("Response 500") {
                mockMvc.perform(
                    MockMvcRequestBuilders.patch("$endpoint/1")
                        .header("Authorization", "Bearer $jwtToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                ).andExpect(MockMvcResultMatchers.status().isInternalServerError)
                    .andReturn()
            }
        }
        When("정상 수정된 경우") {
            val jwtToken = loginAndGetToken()
            var productId: Long = 0
            val productUpdatePayload = ProductUpdatePayload(
                price = 1000,
                description = "world"
            )

            beforeEach {
                val result = productRepository.save(
                    Product(
                        category = "커피",
                        price = 5000,
                        cost = 500,
                        name = "라떼",
                        description = "hello",
                        barcode = "0123456789123",
                        expirationDate = Date.from(Instant.now()),
                        size = ProductSize.LARGE
                    )
                )
                productId = result.id
            }

            afterEach {
                productRepository.deleteAll()
            }

            Then("Response 200, 수정된 결과 응답") {
                mockMvc.perform(
                    MockMvcRequestBuilders.patch("$endpoint/${productId}")
                        .header("Authorization", "Bearer $jwtToken")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(productUpdatePayload))
                )
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(productId))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").value(1000))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value("world"))

            }
        }
    }

    Given("상품 삭제") {
        When("해당 상품이 존재하지 않는 경우") {
            val jwtToken = loginAndGetToken()
            var productId: Long = 0
            beforeEach {
                val result = productRepository.save(
                    Product(
                        category = "커피",
                        price = 5000,
                        cost = 500,
                        name = "라떼",
                        description = "hello",
                        barcode = "0123456789123",
                        expirationDate = Date.from(Instant.now()),
                        size = ProductSize.LARGE
                    )
                )
                productId = result.id
            }

            afterEach {
                productRepository.deleteAll()
            }
            Then("Response 404") {
                mockMvc.perform(
                    MockMvcRequestBuilders.delete("$endpoint/${productId - 100}")
                        .header("Authorization", "Bearer $jwtToken")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding("UTF-8")
                )
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
            }
        }
        When("상품이 정상적으로 삭제(isActive=false) 된 경우") {
            val jwtToken = loginAndGetToken()
            var productId: Long = 0
            beforeEach {
                val result = productRepository.save(
                    Product(
                        category = "커피",
                        price = 5000,
                        cost = 500,
                        name = "라떼",
                        description = "hello",
                        barcode = "0123456789123",
                        expirationDate = Date.from(Instant.now()),
                        size = ProductSize.LARGE
                    )
                )
                productId = result.id
            }

            afterEach {
                productRepository.deleteAll()
            }

            Then("Response") {
                mockMvc.perform(
                    MockMvcRequestBuilders.delete("$endpoint/${productId}")
                        .header("Authorization", "Bearer $jwtToken")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding("UTF-8")
                )
                    .andExpect(MockMvcResultMatchers.status().isOk)
                productRepository.findById(productId).get().isActive shouldBe false
            }
        }
    }
})