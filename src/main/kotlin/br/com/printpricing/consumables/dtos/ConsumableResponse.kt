package br.com.printpricing.consumables.dtos

import br.com.printpricing.consumables.entities.Consumable
import java.math.BigDecimal

data class ConsumableResponse(
    val id: Long?,
    val name: String,
    val unitCost: BigDecimal
) {
    constructor(entity: Consumable) : this(
        id = entity.id,
        name = entity.name,
        unitCost = entity.unitCost
    )
}
