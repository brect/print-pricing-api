package br.com.printpricing.pricing.entities

import br.com.printpricing.marketplaces.entities.Marketplace
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
@Table(name = "pricing_simulation_marketplace_results")
class PricingSimulationMarketplaceResult(
    @Id @GeneratedValue
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    var simulation: PricingSimulation,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_id", nullable = false)
    var marketplace: Marketplace,

    @Column(nullable = false, precision = 12, scale = 2)
    var consumerPrice: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var marketplaceFees: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var taxAmount: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var grossProfit: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var netProfit: BigDecimal
)
