package br.com.printpricing.marketplaces.dtos

import br.com.printpricing.marketplaces.entities.MarketplaceFeeRule
import br.com.printpricing.marketplaces.entities.MarketplaceFeeType
import java.math.BigDecimal

data class MarketplaceFeeRuleResponse(
    val id: Long?,
    val name: String,
    val type: MarketplaceFeeType,
    val percentage: BigDecimal,
    val fixedAmount: BigDecimal
) {
    constructor(rule: MarketplaceFeeRule) : this(
        id = rule.id,
        name = rule.name,
        type = rule.type,
        percentage = rule.percentage,
        fixedAmount = rule.fixedAmount
    )
}
