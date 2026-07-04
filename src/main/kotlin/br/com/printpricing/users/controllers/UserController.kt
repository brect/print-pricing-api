package br.com.printpricing.users.controllers

import br.com.printpricing.security.Jwt
import br.com.printpricing.security.UserToken
import br.com.printpricing.users.dtos.CreateUserRequest
import br.com.printpricing.users.dtos.UpdateUserRequest
import br.com.printpricing.users.dtos.UserResponse
import br.com.printpricing.users.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Cadastro e autenticacao de usuarios")
class UserController(
    private val service: UserService,
    private val jwt: Jwt
) {
    @PostMapping
    @Operation(summary = "Criar usuario")
    fun create(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))

    @GetMapping("/me")
    @Operation(summary = "Retorna o usuario autenticado", security = [SecurityRequirement(name = "jwt-auth")])
    fun me(): ResponseEntity<UserToken> =
        jwt.authenticatedUser()?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do usuario", security = [SecurityRequirement(name = "jwt-auth")])
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: UpdateUserRequest): ResponseEntity<UserResponse> =
        service.update(id, request).let { ResponseEntity.ok(it) }

    @PutMapping("/{id}/roles/{role}")
    @Operation(summary = "Adicionar role ao usuario", security = [SecurityRequirement(name = "jwt-auth")])
    @PreAuthorize("hasRole('ADMIN')")
    fun addRole(@PathVariable id: Long, @PathVariable role: String): ResponseEntity<UserResponse> =
        service.addRole(id, role).let { ResponseEntity.ok(it) }
}
