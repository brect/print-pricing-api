package br.com.printpricing.pricing.dtos

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class SimulationConsumableRequest(
    @field:NotNull val consumableId: Long,
    @field:DecimalMin("0.00") val quantity: BigDecimal
)
