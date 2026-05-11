package br.com.printpricing.consumables.dtos

import br.com.printpricing.consumables.entities.Consumable
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreateConsumableRequest(
    @field:NotBlank val name: String,
    @field:DecimalMin("0.00") val unitCost: BigDecimal
) {
    fun toEntity() = Consumable(
        name = name.trim(),
        unitCost = unitCost
    )
}
