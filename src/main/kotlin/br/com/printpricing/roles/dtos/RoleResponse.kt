package br.com.printpricing.roles.dtos

import br.com.printpricing.roles.entities.Role

data class RoleResponse(
    val id: Long?,
    val name: String,
    val description: String
) {
    constructor(role: Role) : this(
        id = role.id,
        name = role.name,
        description = role.description
    )
}
