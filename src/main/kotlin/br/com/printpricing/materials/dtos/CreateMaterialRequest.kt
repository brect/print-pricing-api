package br.com.printpricing.materials.dtos

import br.com.printpricing.materials.entities.FilamentMaterial
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreateMaterialRequest(
    @field:NotBlank val brand: String,
    @field:NotBlank val type: String,
    @field:DecimalMin("0.01") val spoolCost: BigDecimal,
    @field:DecimalMin("0.001") val spoolWeightKg: BigDecimal,
    val color: String? = null
) {
    fun toMaterial() = FilamentMaterial(
        brand = brand,
        type = type,
        spoolCost = spoolCost,
        spoolWeightKg = spoolWeightKg,
        color = color
    )
}
