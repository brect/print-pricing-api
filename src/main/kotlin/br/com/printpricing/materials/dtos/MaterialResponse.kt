package br.com.printpricing.materials.dtos

import br.com.printpricing.materials.entities.FilamentMaterial
import java.math.BigDecimal

data class MaterialResponse(
    val id: Long?,
    val brand: String,
    val type: String,
    val spoolCost: BigDecimal,
    val spoolWeightKg: BigDecimal,
    val color: String?,
    val costPerKg: BigDecimal
) {
    constructor(material: FilamentMaterial) : this(
        id = material.id,
        brand = material.brand,
        type = material.type,
        spoolCost = material.spoolCost,
        spoolWeightKg = material.spoolWeightKg,
        color = material.color,
        costPerKg = material.costPerKg()
    )
}
