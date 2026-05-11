package br.com.printpricing.marketplaces.dtos

import br.com.printpricing.marketplaces.entities.Marketplace
import br.com.printpricing.marketplaces.entities.MarketplaceFeeRule
import br.com.printpricing.marketplaces.entities.MarketplaceFeeType
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreateMarketplaceFeeRuleRequest(
    @field:NotBlank val name: String,
    val type: MarketplaceFeeType,
    @field:DecimalMin("0.00") val percentage: BigDecimal = BigDecimal.ZERO,
    @field:DecimalMin("0.00") val fixedAmount: BigDecimal = BigDecimal.ZERO
) {
    fun toRule(marketplace: Marketplace) = MarketplaceFeeRule(
        marketplace = marketplace,
        name = name,
        type = type,
        percentage = percentage,
        fixedAmount = fixedAmount
    )
}
