package br.com.printpricing.categories.services

import br.com.printpricing.categories.entities.Category
import br.com.printpricing.categories.repositories.CategoryRepository
import br.com.printpricing.exceptions.ConflictException
import br.com.printpricing.exceptions.ResourceNotFoundException
import br.com.printpricing.products.repositories.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val repository: CategoryRepository,
    private val productRepository: ProductRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun create(payload: Category): Category {
        if (repository.existsByNameIgnoreCase(payload.name)) {
            throw ConflictException("Categoria com mesmo nome ja existe")
        }
        val saved = repository.save(payload)
        log.info("Categoria criada id={} name={}", saved.id, saved.name)
        return saved
    }

    fun findAll(): List<Category> = repository.findAll().sortedBy { it.name }

    fun findById(id: Long): Category =
        repository.findById(id).orElseThrow { ResourceNotFoundException("Categoria", id) }

    @Transactional
    fun update(id: Long, payload: Category): Category {
        val current = findById(id)
        current.name = payload.name.trim()
        current.description = payload.description.trim()
        val saved = repository.save(current)
        log.info("Categoria atualizada id={} name={}", saved.id, saved.name)
        return saved
    }

    @Transactional
    fun delete(id: Long) {
        val current = findById(id)
        productRepository.removeCategoryLinks(id)

        repository.delete(current)
        log.info("Categoria removida id={}", id)
    }
}
