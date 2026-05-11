package br.com.printpricing.categories.dtos

import br.com.printpricing.categories.entities.Category
import jakarta.validation.constraints.NotBlank

data class CreateCategoryRequest(
    @field:NotBlank val name: String,
    val description: String = ""
) {
    fun toEntity() = Category(
        name = name.trim(),
        description = description.trim()
    )
}
