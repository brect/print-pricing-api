package br.com.printpricing.products.dtos

import br.com.printpricing.products.entities.Product
import java.math.BigDecimal

data class ProductResponse(
    val id: Long?,
    val name: String,
    val sku: String,
    val description: String,
    val stlFileUrl: String?,
    val defaultWeightGrams: BigDecimal,
    val defaultPrintMinutes: Int,
    val categories: List<CategorySummaryResponse>
) {
    constructor(product: Product) : this(
        id = product.id,
        name = product.name,
        sku = product.sku,
        description = product.description,
        stlFileUrl = product.stlFileUrl,
        defaultWeightGrams = product.defaultWeightGrams,
        defaultPrintMinutes = product.defaultPrintMinutes,
        categories = product.categories
            .mapNotNull { category -> category.id?.let { CategorySummaryResponse(it, category.name) } }
            .sortedBy { it.name }
    )
}

data class CategorySummaryResponse(
    val id: Long,
    val name: String
)
