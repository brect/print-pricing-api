package br.com.printpricing.pricing.entities

import br.com.printpricing.consumables.entities.Consumable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "pricing_simulation_consumables")
class PricingSimulationConsumable(
    @Id @GeneratedValue
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    var simulation: PricingSimulation,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumable_id")
    var consumable: Consumable? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, precision = 12, scale = 3)
    var quantity: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var unitCost: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var totalCost: BigDecimal
)
