package com.chameleon.product

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.id > :cursor ORDER BY p.id ASC")
    fun findProductsAfterCursor(
        @Param("cursor") cursor: Long,
        pageable: Pageable
    ): List<Product>

    @Query(value = "SELECT * FROM Products WHERE name REGEXP :pattern", nativeQuery = true)
    fun searchByInitialConsonants(@Param("pattern") pattern: String): List<Product>
}