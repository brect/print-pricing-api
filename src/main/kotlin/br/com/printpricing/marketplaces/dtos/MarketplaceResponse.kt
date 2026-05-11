package br.com.printpricing.marketplaces.dtos

import br.com.printpricing.marketplaces.entities.Marketplace

data class MarketplaceResponse(
    val id: Long?,
    val name: String,
    val active: Boolean,
    val feeRules: List<MarketplaceFeeRuleResponse>
) {
    constructor(marketplace: Marketplace) : this(
        id = marketplace.id,
        name = marketplace.name,
        active = marketplace.active,
        feeRules = marketplace.feeRules.map { MarketplaceFeeRuleResponse(it) }
    )
}
