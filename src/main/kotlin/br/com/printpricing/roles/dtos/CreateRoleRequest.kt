package br.com.printpricing.roles.dtos

import jakarta.validation.constraints.NotBlank

data class CreateRoleRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val description: String
)
