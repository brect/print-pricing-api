package br.com.printpricing.pricing.calculators

import br.com.printpricing.expenses.entities.ExpenseAllocationStrategy
import br.com.printpricing.expenses.entities.FixedAsset
import br.com.printpricing.expenses.entities.FixedExpense
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class PricingCostCalculator {
    data class Input(
        val materialCost: BigDecimal,
        val machineCost: BigDecimal,
        val consumablesCost: BigDecimal,
        val manualFixedCost: BigDecimal,
        val laborCost: BigDecimal,
        val failureRatePercent: BigDecimal,
        val units: Int,
        val markupMultiplier: BigDecimal,
        val taxPercent: BigDecimal,
        val printHours: BigDecimal,
        val fixedExpenses: List<FixedExpense>,
        val fixedAssets: List<FixedAsset>
    )

    data class Output(
        val allocatedFixedExpensesCost: BigDecimal,
        val allocatedFixedAssetsCost: BigDecimal,
        val effectiveFixedCost: BigDecimal,
        val failureCost: BigDecimal,
        val totalCost: BigDecimal,
        val unitCost: BigDecimal,
        val consumerPrice: BigDecimal,
        val grossProfit: BigDecimal,
        val taxAmount: BigDecimal,
        val netProfitBeforeMarketplace: BigDecimal
    )

    fun calculate(input: Input): Output {
        val allocatedFixedExpenses = input.fixedExpenses.sumOf {
            allocate(it.monthlyAmount, it.allocationStrategy, input.units, input.printHours, it.monthlyUnitsCapacity, it.monthlyMachineHoursCapacity)
        }.money()

        val allocatedFixedAssets = input.fixedAssets.sumOf {
            val monthlyDepreciation = it.cost.divide(BigDecimal(it.usefulLifeMonths), 6, RoundingMode.HALF_UP)
            allocate(monthlyDepreciation, it.allocationStrategy, input.units, input.printHours, it.monthlyUnitsCapacity, it.monthlyMachineHoursCapacity)
        }.money()

        val effectiveFixedCost = input.manualFixedCost
            .add(allocatedFixedExpenses)
            .add(allocatedFixedAssets)
            .money()

        val baseCost = input.materialCost
            .add(input.machineCost)
            .add(input.consumablesCost)
            .add(effectiveFixedCost)
            .add(input.laborCost)
            .money()

        val failureCost = baseCost.percent(input.failureRatePercent).money()
        val totalCost = baseCost.add(failureCost).money()
        val unitCost = totalCost.divide(BigDecimal(input.units), 2, RoundingMode.HALF_UP)
        val consumerPrice = unitCost.multiply(input.markupMultiplier).money()
        val grossProfit = consumerPrice.subtract(unitCost).money()
        val taxAmount = consumerPrice.percent(input.taxPercent).money()
        val netProfitBeforeMarketplace = grossProfit.subtract(taxAmount).money()

        return Output(
            allocatedFixedExpensesCost = allocatedFixedExpenses,
            allocatedFixedAssetsCost = allocatedFixedAssets,
            effectiveFixedCost = effectiveFixedCost,
            failureCost = failureCost,
            totalCost = totalCost,
            unitCost = unitCost,
            consumerPrice = consumerPrice,
            grossProfit = grossProfit,
            taxAmount = taxAmount,
            netProfitBeforeMarketplace = netProfitBeforeMarketplace
        )
    }

    private fun allocate(
        amount: BigDecimal,
        strategy: ExpenseAllocationStrategy,
        units: Int,
        printHours: BigDecimal,
        monthlyUnitsCapacity: Int,
        monthlyMachineHoursCapacity: BigDecimal
    ): BigDecimal = when (strategy) {
        ExpenseAllocationStrategy.PER_SIMULATION -> amount
        ExpenseAllocationStrategy.PER_UNIT -> amount.divide(BigDecimal(monthlyUnitsCapacity), 6, RoundingMode.HALF_UP).multiply(BigDecimal(units))
        ExpenseAllocationStrategy.PER_MACHINE_HOUR -> amount.divide(monthlyMachineHoursCapacity, 6, RoundingMode.HALF_UP).multiply(printHours)
    }

    private fun BigDecimal.percent(percent: BigDecimal): BigDecimal =
        multiply(percent).divide(BigDecimal(100), 6, RoundingMode.HALF_UP)

    private fun BigDecimal.money(): BigDecimal = setScale(2, RoundingMode.HALF_UP)
}
