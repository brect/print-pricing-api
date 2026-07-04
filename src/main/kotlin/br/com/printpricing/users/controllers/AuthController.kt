package br.com.printpricing.users.controllers

import br.com.printpricing.security.Jwt
import br.com.printpricing.security.UserToken
import br.com.printpricing.users.dtos.ConfirmUserRequest
import br.com.printpricing.users.dtos.LoginPendingResponse
import br.com.printpricing.users.dtos.LoginRequest
import br.com.printpricing.users.dtos.LoginResponse
import br.com.printpricing.users.dtos.UserResponse
import br.com.printpricing.users.services.LoginResult
import br.com.printpricing.users.services.UserAuthService
import br.com.printpricing.users.services.UserConfirmationService
import br.com.printpricing.users.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Tag(name = "Auth", description = "Autenticacao por telefone e confirmacao de usuario")
class AuthController(
    private val authService: UserAuthService,
    private val confirmationService: UserConfirmationService,
    private val userService: UserService,
    private val jwt: Jwt
) {
    @PostMapping("/login")
    @Operation(summary = "Realizar login por telefone e uuid")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Any> =
        when (val result = authService.authenticate(request)) {
            is LoginResult.Authenticated -> {
                val token = jwt.create(UserToken(result.user))
                ResponseEntity.ok(LoginResponse(token = token, user = userService.toResponse(result.user)))
            }
            LoginResult.ConfirmationRequired ->
                ResponseEntity.status(HttpStatus.ACCEPTED).body(LoginPendingResponse())
        }

    @PostMapping("/confirm")
    @Operation(summary = "Confirmar telefone e uuid")
    fun confirm(@Valid @RequestBody request: ConfirmUserRequest): ResponseEntity<UserResponse> =
        ResponseEntity.ok(confirmationService.confirm(request))
}
