package br.com.printpricing.pricing.dtos

import java.math.BigDecimal

data class MarketplacePricingResponse(
    val marketplaceId: Long?,
    val marketplaceName: String,
    val consumerPrice: BigDecimal,
    val marketplaceFees: BigDecimal,
    val taxAmount: BigDecimal,
    val grossProfit: BigDecimal,
    val netProfit: BigDecimal
)
