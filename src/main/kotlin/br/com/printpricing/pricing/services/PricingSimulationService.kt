package br.com.printpricing.pricing.services

import br.com.printpricing.consumables.repositories.ConsumableRepository
import br.com.printpricing.expenses.services.FixedAssetService
import br.com.printpricing.expenses.services.FixedExpenseService
import br.com.printpricing.exceptions.ResourceNotFoundException
import br.com.printpricing.marketplaces.entities.MarketplaceFeeRule
import br.com.printpricing.marketplaces.entities.MarketplaceFeeType
import br.com.printpricing.marketplaces.repositories.MarketplaceRepository
import br.com.printpricing.materials.repositories.FilamentMaterialRepository
import br.com.printpricing.pricing.calculators.PricingCostCalculator
import br.com.printpricing.pricing.dtos.CreatePricingSimulationRequest
import br.com.printpricing.pricing.dtos.MarketplacePricingResponse
import br.com.printpricing.pricing.dtos.PricingSimulationResponse
import br.com.printpricing.pricing.dtos.SimulationConsumableResponse
import br.com.printpricing.pricing.dtos.SimulationCostsResponse
import br.com.printpricing.pricing.dtos.SimulationProductResponse
import br.com.printpricing.pricing.dtos.SimulationPricingResponse
import br.com.printpricing.pricing.entities.PricingSimulation
import br.com.printpricing.pricing.entities.PricingSimulationConsumable
import br.com.printpricing.pricing.entities.PricingSimulationMarketplaceResult
import br.com.printpricing.pricing.repositories.PricingSimulationRepository
import br.com.printpricing.printers.repositories.PrinterRepository
import br.com.printpricing.products.repositories.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class PricingSimulationService(
    private val productRepository: ProductRepository,
    private val printerRepository: PrinterRepository,
    private val materialRepository: FilamentMaterialRepository,
    private val consumableRepository: ConsumableRepository,
    private val fixedExpenseService: FixedExpenseService,
    private val fixedAssetService: FixedAssetService,
    private val costCalculator: PricingCostCalculator,
    private val marketplaceRepository: MarketplaceRepository,
    private val simulationRepository: PricingSimulationRepository
) {
    @Transactional
    fun simulate(request: CreatePricingSimulationRequest): PricingSimulationResponse {
        val product = productRepository.findById(request.productId)
            .orElseThrow { ResourceNotFoundException("Produto", request.productId) }
        val printer = printerRepository.findById(request.printerId)
            .orElseThrow { ResourceNotFoundException("Impressora", request.printerId) }
        val material = materialRepository.findById(request.materialId)
            .orElseThrow { ResourceNotFoundException("Material", request.materialId) }

        val weightGrams = request.weightGrams ?: product.defaultWeightGrams
        val printMinutes = request.printMinutes ?: product.defaultPrintMinutes
        val printHours = BigDecimal(printMinutes).divide(BigDecimal(60), 6, RoundingMode.HALF_UP)

        val materialCost = material.costPerKg()
            .multiply(weightGrams.divide(BigDecimal(1000), 6, RoundingMode.HALF_UP))
            .money()
        val depreciationCost = printer.depreciationPerHour().multiply(printHours).money()
        val electricityCost = printer.consumptionKw.multiply(printHours).multiply(request.energyKwhCost).money()
        val machineCost = depreciationCost.add(electricityCost).money()
        val resolvedConsumables = request.consumables.map { item ->
            val catalogItem = consumableRepository.findById(item.consumableId)
                .orElseThrow { ResourceNotFoundException("Consumivel", item.consumableId) }
            Triple(catalogItem, catalogItem.name, catalogItem.unitCost.money())
        }

        val consumablesCost = request.consumables
            .zip(resolvedConsumables)
                .fold(BigDecimal.ZERO) { total, (item, resolved) -> total.add(item.quantity.multiply(resolved.third)) }
            .money()
        val fixedExpenses = fixedExpenseService.findActive()
        val fixedAssets = fixedAssetService.findActive()
        val calculated = costCalculator.calculate(
            PricingCostCalculator.Input(
                materialCost = materialCost,
                machineCost = machineCost,
                consumablesCost = consumablesCost,
                manualFixedCost = request.fixedCost.money(),
                laborCost = request.laborCost.money(),
                failureRatePercent = request.failureRatePercent,
                units = request.units,
                markupMultiplier = request.markupMultiplier,
                taxPercent = request.taxPercent,
                printHours = printHours,
                fixedExpenses = fixedExpenses,
                fixedAssets = fixedAssets
            )
        )

        val marketplaceIds = request.marketplaceIds.toSet()
        val marketplaces = if (marketplaceIds.isEmpty()) {
            marketplaceRepository.findByActiveTrue()
        } else {
            marketplaceRepository.findAllById(marketplaceIds).filter { it.active }
        }

        val simulation = PricingSimulation(
            name = request.name ?: "Simulacao ${product.name}",
            notes = request.notes,
            product = product,
            printer = printer,
            material = material,
            weightGrams = weightGrams,
            printMinutes = printMinutes,
            energyKwhCost = request.energyKwhCost.money(),
            failureRatePercent = request.failureRatePercent,
            fixedCost = calculated.effectiveFixedCost,
            laborCost = request.laborCost.money(),
            units = request.units,
            markupMultiplier = request.markupMultiplier,
            taxPercent = request.taxPercent,
            materialCost = materialCost,
            depreciationCost = depreciationCost,
            electricityCost = electricityCost,
            machineCost = machineCost,
            consumablesCost = consumablesCost,
            allocatedFixedExpensesCost = calculated.allocatedFixedExpensesCost,
            allocatedFixedAssetsCost = calculated.allocatedFixedAssetsCost,
            failureCost = calculated.failureCost,
            totalCost = calculated.totalCost,
            unitCost = calculated.unitCost,
            consumerPrice = calculated.consumerPrice,
            grossProfit = calculated.grossProfit,
            taxAmount = calculated.taxAmount,
            netProfitBeforeMarketplace = calculated.netProfitBeforeMarketplace
        )

        simulation.consumables = request.consumables.zip(resolvedConsumables).map { (item, resolved) ->
            PricingSimulationConsumable(
                simulation = simulation,
                consumable = resolved.first,
                name = resolved.second,
                quantity = item.quantity,
                unitCost = resolved.third,
                totalCost = item.quantity.multiply(resolved.third).money()
            )
        }.toMutableList()

        simulation.marketplaceResults = marketplaces.map { marketplace ->
            val fees = marketplace.feeRules.fold(BigDecimal.ZERO) { total, rule ->
                total.add(rule.calculate(calculated.consumerPrice))
            }.money()
            PricingSimulationMarketplaceResult(
                simulation = simulation,
                marketplace = marketplace,
                consumerPrice = calculated.consumerPrice,
                marketplaceFees = fees,
                taxAmount = calculated.taxAmount,
                grossProfit = calculated.grossProfit,
                netProfit = calculated.grossProfit.subtract(calculated.taxAmount).subtract(fees).money()
            )
        }.toMutableList()

        return simulationRepository.saveAndFlush(simulation).toResponse()
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): PricingSimulationResponse =
        simulationRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Simulacao de precificacao", id) }
            .toResponse()

    @Transactional(readOnly = true)
    fun findByProduct(productId: Long): List<PricingSimulationResponse> =
        simulationRepository.findByProductIdOrderByCreatedAtDesc(productId).map { it.toResponse() }

    private fun PricingSimulation.toResponse(): PricingSimulationResponse =
        PricingSimulationResponse(
            id = id,
            name = name,
            notes = notes,
            product = SimulationProductResponse(
                productName = product.name,
                stlFileUrl = product.stlFileUrl,
                printerName = printer.name,
                materialName = "${material.brand} ${material.type}",
                weightGrams = weightGrams,
                printMinutes = printMinutes,
                units = units
            ),
            costs = SimulationCostsResponse(
                material = materialCost,
                depreciation = depreciationCost,
                electricity = electricityCost,
                machineTotal = machineCost,
                consumables = consumablesCost,
                fixedExpensesAllocated = allocatedFixedExpensesCost,
                fixedAssetsAllocated = allocatedFixedAssetsCost,
                fixed = fixedCost,
                labor = laborCost,
                failures = failureCost,
                total = totalCost,
                unit = unitCost
            ),
            pricing = SimulationPricingResponse(
                markupMultiplier = markupMultiplier,
                consumerPrice = consumerPrice,
                grossProfit = grossProfit,
                taxAmount = taxAmount,
                netProfitBeforeMarketplace = netProfitBeforeMarketplace
            ),
            consumables = consumables.map {
                SimulationConsumableResponse(
                    id = it.id,
                    consumableId = it.consumable?.id,
                    name = it.name,
                    quantity = it.quantity,
                    unitCost = it.unitCost,
                    totalCost = it.totalCost
                )
            },
            marketplaces = marketplaceResults.map {
                MarketplacePricingResponse(
                    marketplaceId = it.marketplace.id,
                    marketplaceName = it.marketplace.name,
                    consumerPrice = it.consumerPrice,
                    marketplaceFees = it.marketplaceFees,
                    taxAmount = it.taxAmount,
                    grossProfit = it.grossProfit,
                    netProfit = it.netProfit
                )
            },
            createdAt = createdAt
        )

    private fun MarketplaceFeeRule.calculate(price: BigDecimal): BigDecimal =
        when (type) {
            MarketplaceFeeType.PERCENTAGE -> price.percent(percentage)
            MarketplaceFeeType.FIXED_AMOUNT -> fixedAmount
            MarketplaceFeeType.PERCENTAGE_PLUS_FIXED -> price.percent(percentage).add(fixedAmount)
        }

    private fun BigDecimal.percent(percent: BigDecimal): BigDecimal =
        multiply(percent).divide(BigDecimal(100), 6, RoundingMode.HALF_UP)

    private fun BigDecimal.money(): BigDecimal = setScale(2, RoundingMode.HALF_UP)
}
