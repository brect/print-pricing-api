package br.com.printpricing.expenses.controllers

import br.com.printpricing.expenses.dtos.CreateFixedAssetRequest
import br.com.printpricing.expenses.dtos.FixedAssetResponse
import br.com.printpricing.expenses.services.FixedAssetService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/expenses/assets")
@Tag(name = "Expenses", description = "Despesas e ativos para rateio no custo")
@SecurityRequirement(name = "jwt-auth")
class FixedAssetController(private val service: FixedAssetService) {
    @PostMapping
    @Operation(summary = "Criar ativo fixo")
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@Valid @RequestBody request: CreateFixedAssetRequest): ResponseEntity<FixedAssetResponse> =
        service.create(request.toEntity()).let { FixedAssetResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    @Operation(summary = "Listar ativos fixos")
    fun list(): ResponseEntity<List<FixedAssetResponse>> =
        service.findAll().map { FixedAssetResponse(it) }
            .let { ResponseEntity.ok(it) }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar ativo fixo")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateFixedAssetRequest
    ): ResponseEntity<FixedAssetResponse> =
        service.update(id, request.toEntity()).let { FixedAssetResponse(it) }
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover ativo fixo")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        service.delete(id).let { ResponseEntity.noContent().build() }
}
