package br.com.printpricing.products.services

import br.com.printpricing.categories.repositories.CategoryRepository
import br.com.printpricing.exceptions.ResourceNotFoundException
import br.com.printpricing.products.entities.Product
import br.com.printpricing.products.repositories.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val repository: ProductRepository,
    private val categoryRepository: CategoryRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun create(product: Product): Product {
        val saved = repository.save(product)
        log.info("Produto criado id={} sku={}", saved.id, saved.sku)
        return saved
    }

    fun findAll(
        name: String?,
        categoryId: Long?,
        sortBy: String,
        direction: String
    ): List<Product> {
        val filtered = repository.findAll().asSequence()
            .filter { name.isNullOrBlank() || it.name.contains(name, ignoreCase = true) }
            .filter { categoryId == null || it.categories.any { category -> category.id == categoryId } }

        val comparator = when (sortBy.lowercase()) {
            "sku" -> compareBy<Product> { it.sku.lowercase() }
            "createdat" -> compareBy<Product> { it.createdAt }
            else -> compareBy<Product> { it.name.lowercase() }
        }

        val sorted = if (direction.equals("DESC", ignoreCase = true)) filtered.sortedWith(comparator.reversed())
        else filtered.sortedWith(comparator)

        return sorted.toList()
    }

    fun findById(id: Long): Product =
        repository.findById(id).orElseThrow { ResourceNotFoundException("Produto", id) }

    @Transactional
    fun update(id: Long, payload: Product): Product {
        val current = findById(id)
        current.name = payload.name
        current.sku = payload.sku
        current.description = payload.description
        current.stlFileUrl = payload.stlFileUrl
        current.defaultWeightGrams = payload.defaultWeightGrams
        current.defaultPrintMinutes = payload.defaultPrintMinutes
        val saved = repository.save(current)
        log.info("Produto atualizado id={} sku={}", saved.id, saved.sku)
        return saved
    }

    @Transactional
    fun delete(id: Long) {
        repository.delete(findById(id))
        log.info("Produto removido id={}", id)
    }

    @Transactional
    fun addCategory(productId: Long, categoryId: Long): Product {
        val product = findById(productId)
        val category = categoryRepository.findById(categoryId)
            .orElseThrow { ResourceNotFoundException("Categoria", categoryId) }
        product.categories.add(category)
        val saved = repository.save(product)
        log.info("Categoria associada productId={} categoryId={}", productId, categoryId)
        return saved
    }

    @Transactional
    fun removeCategory(productId: Long, categoryId: Long): Product {
        val product = findById(productId)
        product.categories.removeIf { it.id == categoryId }
        val saved = repository.save(product)
        log.info("Categoria removida productId={} categoryId={}", productId, categoryId)
        return saved
    }
}
