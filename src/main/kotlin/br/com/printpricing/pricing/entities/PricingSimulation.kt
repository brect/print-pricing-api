package br.com.printpricing.pricing.entities

import br.com.printpricing.materials.entities.FilamentMaterial
import br.com.printpricing.printers.entities.Printer
import br.com.printpricing.products.entities.Product
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "pricing_simulations")
class PricingSimulation(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var notes: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "printer_id", nullable = false)
    var printer: Printer,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    var material: FilamentMaterial,

    @Column(nullable = false, precision = 10, scale = 2)
    var weightGrams: BigDecimal,

    @Column(nullable = false)
    var printMinutes: Int,

    @Column(nullable = false, precision = 8, scale = 2)
    var energyKwhCost: BigDecimal,

    @Column(nullable = false, precision = 8, scale = 2)
    var failureRatePercent: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var fixedCost: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var laborCost: BigDecimal,

    @Column(nullable = false)
    var units: Int,

    @Column(nullable = false, precision = 8, scale = 2)
    var markupMultiplier: BigDecimal,

    @Column(nullable = false, precision = 8, scale = 2)
    var taxPercent: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var materialCost: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var depreciationCost: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var electricityCost: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var machineCost: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var consumablesCost: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var allocatedFixedExpensesCost: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 12, scale = 2)
    var allocatedFixedAssetsCost: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 12, scale = 2)
    var failureCost: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var totalCost: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var unitCost: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var consumerPrice: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var grossProfit: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var taxAmount: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var netProfitBeforeMarketplace: BigDecimal,

    @OneToMany(mappedBy = "simulation", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var consumables: MutableList<PricingSimulationConsumable> = mutableListOf(),

    @OneToMany(mappedBy = "simulation", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var marketplaceResults: MutableList<PricingSimulationMarketplaceResult> = mutableListOf(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
)
