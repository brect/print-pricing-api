package br.com.printpricing.marketplaces.dtos

import br.com.printpricing.marketplaces.entities.Marketplace
import jakarta.validation.constraints.NotBlank

data class CreateMarketplaceRequest(
    @field:NotBlank val name: String,
    val active: Boolean = true,
    val feeRules: List<CreateMarketplaceFeeRuleRequest> = emptyList()
) {
    fun toMarketplace(): Marketplace =
        Marketplace(name = name, active = active).also { marketplace ->
            marketplace.feeRules = feeRules.map { it.toRule(marketplace) }.toMutableList()
        }
}
