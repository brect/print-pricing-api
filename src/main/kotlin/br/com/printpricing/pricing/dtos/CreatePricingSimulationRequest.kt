package br.com.printpricing.pricing.dtos

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreatePricingSimulationRequest(
    val name: String? = null,
    val notes: String = "",
    @field:NotNull val productId: Long,
    @field:NotNull val printerId: Long,
    @field:NotNull val materialId: Long,
    @field:DecimalMin("0.00") val weightGrams: BigDecimal? = null,
    @field:Min(0) val printMinutes: Int? = null,
    @field:DecimalMin("0.00") val energyKwhCost: BigDecimal = BigDecimal("0.90"),
    @field:DecimalMin("0.00") val failureRatePercent: BigDecimal = BigDecimal.ZERO,
    @field:DecimalMin("0.00") val fixedCost: BigDecimal = BigDecimal.ZERO,
    @field:DecimalMin("0.00") val laborCost: BigDecimal = BigDecimal.ZERO,
    @field:Min(1) val units: Int = 1,
    @field:DecimalMin("0.00") val markupMultiplier: BigDecimal = BigDecimal("2.00"),
    @field:DecimalMin("0.00") val taxPercent: BigDecimal = BigDecimal.ZERO,
    @field:Valid val consumables: List<SimulationConsumableRequest> = emptyList(),
    val marketplaceIds: List<Long> = emptyList()
)
