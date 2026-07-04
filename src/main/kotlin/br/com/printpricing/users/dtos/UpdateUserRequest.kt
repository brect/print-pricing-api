package br.com.printpricing.users.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
    @field:Email
    val email: String? = null,
    @field:Size(min = 1)
    val name: String? = null,
    val description: String? = null
)
