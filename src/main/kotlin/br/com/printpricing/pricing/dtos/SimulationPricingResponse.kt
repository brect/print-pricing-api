package br.com.printpricing.pricing.dtos

import java.math.BigDecimal

data class SimulationPricingResponse(
    val markupMultiplier: BigDecimal,
    val consumerPrice: BigDecimal,
    val grossProfit: BigDecimal,
    val taxAmount: BigDecimal,
    val netProfitBeforeMarketplace: BigDecimal
)
