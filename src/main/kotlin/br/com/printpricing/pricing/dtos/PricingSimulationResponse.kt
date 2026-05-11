package br.com.printpricing.pricing.dtos

import java.time.LocalDateTime

data class PricingSimulationResponse(
    val id: Long?,
    val name: String,
    val notes: String,
    val product: SimulationProductResponse,
    val costs: SimulationCostsResponse,
    val pricing: SimulationPricingResponse,
    val consumables: List<SimulationConsumableResponse>,
    val marketplaces: List<MarketplacePricingResponse>,
    val createdAt: LocalDateTime?
)
