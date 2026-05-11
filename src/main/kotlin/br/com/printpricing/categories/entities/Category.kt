package br.com.printpricing.categories.entities

import br.com.printpricing.products.entities.Product
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "categories")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false)
    var description: String = "",

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    var products: MutableSet<Product> = mutableSetOf()
)
