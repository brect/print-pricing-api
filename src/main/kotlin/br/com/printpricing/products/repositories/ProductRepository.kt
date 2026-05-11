package br.com.printpricing.products.repositories

import br.com.printpricing.products.entities.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>
