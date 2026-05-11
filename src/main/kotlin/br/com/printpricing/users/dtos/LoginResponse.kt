package br.com.printpricing.users.dtos

data class LoginResponse(
    val token: String,
    val user: UserResponse
)
