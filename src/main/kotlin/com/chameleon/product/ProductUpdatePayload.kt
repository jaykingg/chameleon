package com.chameleon.product

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ProductUpdatePayload(
    @field: Min(0)
    var price: Int,

    @field: NotBlank
    @field: Size(max = 1024)
    var description: String
)