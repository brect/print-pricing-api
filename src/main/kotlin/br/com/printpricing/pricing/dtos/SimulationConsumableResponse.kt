package br.com.printpricing.pricing.dtos

import java.math.BigDecimal

data class SimulationConsumableResponse(
    val id: Long?,
    val consumableId: Long?,
    val name: String,
    val quantity: BigDecimal,
    val unitCost: BigDecimal,
    val totalCost: BigDecimal
)
