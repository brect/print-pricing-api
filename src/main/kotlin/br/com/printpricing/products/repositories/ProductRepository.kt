package br.com.printpricing.products.repositories

import br.com.printpricing.products.entities.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {
    @Modifying
    @Query(value = "delete from product_categories where category_id = :categoryId", nativeQuery = true)
    fun removeCategoryLinks(@Param("categoryId") categoryId: Long): Int
}
