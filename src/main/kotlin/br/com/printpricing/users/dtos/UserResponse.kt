package br.com.printpricing.users.dtos

data class UserResponse(
    val id: Long?,
    val email: String?,
    val phone: String?,
    val name: String,
    val description: String?,
    val active: Boolean,
    val roles: Set<String>
)
