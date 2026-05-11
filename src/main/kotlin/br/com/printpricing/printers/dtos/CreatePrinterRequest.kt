package br.com.printpricing.printers.dtos

import br.com.printpricing.printers.entities.Printer
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreatePrinterRequest(
    @field:NotBlank val name: String,
    @field:DecimalMin("0.01") val purchasePrice: BigDecimal,
    @field:DecimalMin("0.00") val maintenanceCost: BigDecimal = BigDecimal.ZERO,
    @field:Min(1) val usefulLifeHours: Int,
    @field:DecimalMin("0.001") val consumptionKw: BigDecimal
) {
    fun toPrinter() = Printer(
        name = name,
        purchasePrice = purchasePrice,
        maintenanceCost = maintenanceCost,
        usefulLifeHours = usefulLifeHours,
        consumptionKw = consumptionKw
    )
}
