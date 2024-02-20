package com.chameleon.product

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(private val productRepository: ProductRepository) {

    @Transactional
    fun createProduct(productPayload: ProductPayload): Product {
        return productRepository.save(
            Product(
                category = productPayload.category,
                price = productPayload.price,
                cost = productPayload.cost,
                name = productPayload.name,
                description = productPayload.description,
                barcode = productPayload.barcode,
                expirationDate = productPayload.expirationDate,
                size = productPayload.size
            )
        )
    }

    @Transactional(readOnly = true)
    fun getProductById(id: Long): Product? {
        return productRepository.findById(id).filter { it.isActive }.orElse(null)
    }


    @Transactional(readOnly = true)
    fun getProducts(cursor: Long?, pageSize: Int = 10): List<Product> {
        val pageRequest = PageRequest.of(0, pageSize)
        return productRepository.findProductsAfterCursor(cursor ?: 0L, pageRequest).filter { it.isActive }
    }

    @Transactional
    fun updateProduct(id: Long, productUpdatePayload: ProductUpdatePayload): Product? {
        val product = productRepository.findById(id).orElse(null)
        product?.let {
            it.price = productUpdatePayload.price
            it.description = productUpdatePayload.description
            return productRepository.save(it)
        }
        return null
    }

    @Transactional
    fun deactivateProduct(id: Long): Boolean {
        val product = productRepository.findById(id).orElse(null) ?: return false
        product.isActive = false
        productRepository.save(product)
        return true
    }

    @Transactional(readOnly = true)
    fun searchProducts(name: String): List<Product> {
        val initialConsonantString = convertToRegexPattern(name)
        return productRepository.searchByInitialConsonants(initialConsonantString)
    }

    private fun convertToRegexPattern(initialConsonant: String): String {
        val consonantMap = mapOf(
            'ㄱ' to "[가-깋]",
            'ㄴ' to "[나-닣]",
            'ㄷ' to "[다-딯]",
            'ㄹ' to "[라-맇]",
            'ㅁ' to "[마-밓]",
            'ㅂ' to "[바-빗]",
            'ㅅ' to "[사-싷]",
            'ㅇ' to "[아-잏]",
            'ㅈ' to "[자-짛]",
            'ㅊ' to "[차-칳]",
            'ㅋ' to "[카-킿]",
            'ㅌ' to "[타-팋]",
            'ㅍ' to "[파-핗]",
            'ㅎ' to "[하-힣]"
        )

        val uppercaseInput = initialConsonant.toUpperCase()
        val regexBuilder = StringBuilder()
        uppercaseInput.forEach {
            regexBuilder.append(consonantMap[it] ?: it)
        }
        return regexBuilder.toString()
    }
}

