package br.com.printpricing.printers.dtos

import br.com.printpricing.printers.entities.Printer
import java.math.BigDecimal

data class PrinterResponse(
    val id: Long?,
    val name: String,
    val purchasePrice: BigDecimal,
    val maintenanceCost: BigDecimal,
    val usefulLifeHours: Int,
    val consumptionKw: BigDecimal,
    val depreciationPerHour: BigDecimal
) {
    constructor(printer: Printer) : this(
        id = printer.id,
        name = printer.name,
        purchasePrice = printer.purchasePrice,
        maintenanceCost = printer.maintenanceCost,
        usefulLifeHours = printer.usefulLifeHours,
        consumptionKw = printer.consumptionKw,
        depreciationPerHour = printer.depreciationPerHour()
    )
}
