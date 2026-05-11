package br.com.printpricing.users.dtos

data class UserResponse(
    val id: Long?,
    val email: String,
    val name: String,
    val roles: Set<String>
)
