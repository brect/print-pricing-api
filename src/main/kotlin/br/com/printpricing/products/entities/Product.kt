package br.com.printpricing.products.entities

import br.com.printpricing.pricing.entities.PricingSimulation
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
class Product(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var sku: String = "",

    @Column(nullable = false)
    var description: String = "",

    @Column
    var stlFileUrl: String? = null,

    @Column(nullable = false, precision = 10, scale = 2)
    var defaultWeightGrams: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    var defaultPrintMinutes: Int = 0,

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var pricingSimulations: MutableList<PricingSimulation> = mutableListOf(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
)
