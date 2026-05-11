package br.com.printpricing.pricing.dtos

import java.math.BigDecimal

data class SimulationCostsResponse(
    val material: BigDecimal,
    val depreciation: BigDecimal,
    val electricity: BigDecimal,
    val machineTotal: BigDecimal,
    val consumables: BigDecimal,
    val fixedExpensesAllocated: BigDecimal,
    val fixedAssetsAllocated: BigDecimal,
    val fixed: BigDecimal,
    val labor: BigDecimal,
    val failures: BigDecimal,
    val total: BigDecimal,
    val unit: BigDecimal
)
