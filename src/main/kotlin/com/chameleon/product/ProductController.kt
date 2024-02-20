package com.chameleon.product

import com.chameleon.config.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    /**
     * 상품 등록
     */
    @PostMapping("/register")
    fun createProduct(
        @Valid @RequestBody productPayload: ProductPayload
    ): ResponseEntity<ApiResponse<Product>> {
        val createdProduct = productService.createProduct(productPayload)
        return ResponseEntity.ok(
            ApiResponse(
                meta = ApiResponse.Meta(code = HttpStatus.CREATED.value(), message = "상품이 성공적으로 등록되었습니다."),
                data = createdProduct
            )
        )
    }

    /**
     * 단일 상품 조회
     */
    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Product>> {
        val product = productService.getProductById(id)
        return ResponseEntity.ok(
            ApiResponse(
                meta = ApiResponse.Meta(code = HttpStatus.OK.value(), message = "상품이 조회되었습니다."),
                data = product
            )
        )
    }

    /**
     * 여러 상품 조회, cursor 기반 Pagination 처리, 10개 default
     */
    @GetMapping("/list")
    fun getProducts(
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<List<Product>>> {
        val products = productService.getProducts(cursor, size)
        return ResponseEntity.ok(
            ApiResponse(
                meta = ApiResponse.Meta(code = HttpStatus.OK.value(), message = "상품들이 조회되었습니다."),
                data = products
            )
        )
    }

    /**
     * 상품 검색
     */
    @GetMapping("/search")
    fun searchProducts(
        @RequestParam name: String
    ): ResponseEntity<ApiResponse<List<Product>>> {
        val products = productService.searchProducts(name)
        return ResponseEntity.ok(
            ApiResponse(
                meta = ApiResponse.Meta(code = HttpStatus.OK.value(), message = "해당 상품이 검색되었습니다."),
                data = products
            )
        )
    }

    /**
     * 상품 수정
     */
    @PatchMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody
        productUpdatePayload: ProductUpdatePayload
    ): ResponseEntity<ApiResponse<Product>> {
        val updatedProduct = productService.updateProduct(id, productUpdatePayload)
        return ResponseEntity.ok(
            ApiResponse(
                meta = ApiResponse.Meta(code = HttpStatus.OK.value(), message = "상품이 수정되었습니다."),
                data = updatedProduct
            )
        )
    }

    /**
     * 상품 삭제
     * 추후 트래킹을 위하여 실제로 데이터베이스에서 삭제되는 것이 아닌, isActive 변수를 활용하여 응답여부 결정
     */
    @DeleteMapping("/{id}")
    fun deactivateProduct(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Void>> {
        val success = productService.deactivateProduct(id)
        return if (success) {
            ResponseEntity.ok(
                ApiResponse(
                    meta = ApiResponse.Meta(code = HttpStatus.OK.value(), message = "싱픔이 삭제되었습니다."),
                    data = null
                )
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }
}