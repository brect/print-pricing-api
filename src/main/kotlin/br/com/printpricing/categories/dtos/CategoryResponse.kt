package br.com.printpricing.categories.dtos

import br.com.printpricing.categories.entities.Category

data class CategoryResponse(
    val id: Long?,
    val name: String,
    val description: String
) {
    constructor(entity: Category) : this(
        id = entity.id,
        name = entity.name,
        description = entity.description
    )
}
