package br.com.printpricing.expenses.dtos

import br.com.printpricing.expenses.entities.ExpenseAllocationStrategy
import br.com.printpricing.expenses.entities.FixedAsset
import br.com.printpricing.expenses.entities.FixedExpense
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreateFixedExpenseRequest(
    @field:NotBlank val name: String,
    @field:DecimalMin("0.00") val monthlyAmount: BigDecimal,
    @field:NotNull val allocationStrategy: ExpenseAllocationStrategy,
    val active: Boolean = true,
    @field:Min(1) val monthlyUnitsCapacity: Int = 100,
    @field:DecimalMin("0.01") val monthlyMachineHoursCapacity: BigDecimal = BigDecimal("160.00")
) {
    fun toEntity() = FixedExpense(
        name = name.trim(),
        monthlyAmount = monthlyAmount,
        allocationStrategy = allocationStrategy,
        active = active,
        monthlyUnitsCapacity = monthlyUnitsCapacity,
        monthlyMachineHoursCapacity = monthlyMachineHoursCapacity
    )
}

data class FixedExpenseResponse(
    val id: Long?,
    val name: String,
    val monthlyAmount: BigDecimal,
    val allocationStrategy: ExpenseAllocationStrategy,
    val active: Boolean,
    val monthlyUnitsCapacity: Int,
    val monthlyMachineHoursCapacity: BigDecimal
) {
    constructor(entity: FixedExpense) : this(
        id = entity.id,
        name = entity.name,
        monthlyAmount = entity.monthlyAmount,
        allocationStrategy = entity.allocationStrategy,
        active = entity.active,
        monthlyUnitsCapacity = entity.monthlyUnitsCapacity,
        monthlyMachineHoursCapacity = entity.monthlyMachineHoursCapacity
    )
}

data class CreateFixedAssetRequest(
    @field:NotBlank val name: String,
    @field:DecimalMin("0.00") val cost: BigDecimal,
    @field:Min(1) val usefulLifeMonths: Int,
    @field:NotNull val allocationStrategy: ExpenseAllocationStrategy,
    val active: Boolean = true,
    @field:Min(1) val monthlyUnitsCapacity: Int = 100,
    @field:DecimalMin("0.01") val monthlyMachineHoursCapacity: BigDecimal = BigDecimal("160.00")
) {
    fun toEntity() = FixedAsset(
        name = name.trim(),
        cost = cost,
        usefulLifeMonths = usefulLifeMonths,
        allocationStrategy = allocationStrategy,
        active = active,
        monthlyUnitsCapacity = monthlyUnitsCapacity,
        monthlyMachineHoursCapacity = monthlyMachineHoursCapacity
    )
}

data class FixedAssetResponse(
    val id: Long?,
    val name: String,
    val cost: BigDecimal,
    val usefulLifeMonths: Int,
    val allocationStrategy: ExpenseAllocationStrategy,
    val active: Boolean,
    val monthlyUnitsCapacity: Int,
    val monthlyMachineHoursCapacity: BigDecimal
) {
    constructor(entity: FixedAsset) : this(
        id = entity.id,
        name = entity.name,
        cost = entity.cost,
        usefulLifeMonths = entity.usefulLifeMonths,
        allocationStrategy = entity.allocationStrategy,
        active = entity.active,
        monthlyUnitsCapacity = entity.monthlyUnitsCapacity,
        monthlyMachineHoursCapacity = entity.monthlyMachineHoursCapacity
    )
}
