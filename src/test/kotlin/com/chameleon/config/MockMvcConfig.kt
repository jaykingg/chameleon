package com.chameleon.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.nio.charset.StandardCharsets

@Configuration
class MockMvcConfig {
    @Bean
    fun mockMvc(webApplicationContext: WebApplicationContext): MockMvc {
        val converter = StringHttpMessageConverter(StandardCharsets.UTF_8)
        converter.setWriteAcceptCharset(false)
        return MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .build()
    }
}