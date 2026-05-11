package br.com.printpricing.products.services

import br.com.printpricing.exceptions.ResourceNotFoundException
import br.com.printpricing.products.entities.Product
import br.com.printpricing.products.repositories.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(private val repository: ProductRepository) {
    fun create(product: Product): Product = repository.save(product)
    fun findAll(): List<Product> = repository.findAll()
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
        return repository.save(current)
    }

    @Transactional
    fun delete(id: Long) {
        repository.delete(findById(id))
    }
}
