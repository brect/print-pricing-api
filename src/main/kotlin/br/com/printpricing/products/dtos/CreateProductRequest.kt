package br.com.printpricing.products.dtos

import br.com.printpricing.products.entities.Product
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreateProductRequest(
    @field:NotBlank val name: String,
    val sku: String = "",
    val description: String = "",
    val stlFileUrl: String? = null,
    @field:DecimalMin("0.00") val defaultWeightGrams: BigDecimal = BigDecimal.ZERO,
    @field:Min(0) val defaultPrintMinutes: Int = 0
) {
    fun toProduct() = Product(
        name = name,
        sku = sku,
        description = description,
        stlFileUrl = stlFileUrl,
        defaultWeightGrams = defaultWeightGrams,
        defaultPrintMinutes = defaultPrintMinutes
    )
}
