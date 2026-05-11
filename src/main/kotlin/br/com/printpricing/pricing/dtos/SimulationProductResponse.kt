package br.com.printpricing.pricing.dtos

import java.math.BigDecimal

data class SimulationProductResponse(
    val productName: String,
    val stlFileUrl: String?,
    val printerName: String,
    val materialName: String,
    val weightGrams: BigDecimal,
    val printMinutes: Int,
    val units: Int
)
