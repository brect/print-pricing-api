package br.com.printpricing.users.dtos

data class LoginPendingResponse(
    val status: String = "CONFIRMATION_REQUIRED",
    val message: String = "Codigo de confirmacao enviado por SMS"
)
