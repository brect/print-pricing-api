package br.com.printpricing.users.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserRequest(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    @field:Size(min = 8)
    val password: String,
    @field:NotBlank
    val name: String
)
