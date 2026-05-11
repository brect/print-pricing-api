package br.com.printpricing.categories.repositories

import br.com.printpricing.categories.entities.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
    fun existsByNameIgnoreCase(name: String): Boolean
}
