package br.com.printpricing.roles.controllers

import br.com.printpricing.roles.dtos.CreateRoleRequest
import br.com.printpricing.roles.dtos.RoleResponse
import br.com.printpricing.roles.services.RoleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "Gerenciamento de perfis de acesso")
@SecurityRequirement(name = "jwt-auth")
@PreAuthorize("hasRole('ADMIN')")
class RoleController(private val service: RoleService) {
    @PostMapping
    @Operation(summary = "Criar role")
    fun create(@Valid @RequestBody request: CreateRoleRequest): ResponseEntity<RoleResponse> =
        service.create(request).let { RoleResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    @Operation(summary = "Listar roles")
    fun list(): ResponseEntity<List<RoleResponse>> =
        service.findAll().map { RoleResponse(it) }
            .let { ResponseEntity.ok(it) }
}
