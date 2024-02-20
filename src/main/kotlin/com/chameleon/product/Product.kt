package com.chameleon.product

import jakarta.persistence.*
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field: NotBlank
    var category: String,

    @field: Min(0)
    var price: Int,

    @field: Min(0)
    var cost: Int,

    @field: NotBlank
    var name: String,

    @field: NotBlank
    @field: Size(max = 1024)
    var description: String,

    @field: NotBlank
    var barcode: String,

    @field: Temporal(TemporalType.DATE)
    var expirationDate: Date,

    @field: Valid
    var size: ProductSize,

    var isActive: Boolean = true
)