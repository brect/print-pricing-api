package br.com.printpricing.expenses.controllers

import br.com.printpricing.expenses.dtos.CreateFixedExpenseRequest
import br.com.printpricing.expenses.dtos.FixedExpenseResponse
import br.com.printpricing.expenses.services.FixedExpenseService
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
@RequestMapping("/expenses/fixed")
@Tag(name = "Expenses", description = "Despesas e ativos para rateio no custo")
@SecurityRequirement(name = "jwt-auth")
class FixedExpenseController(private val service: FixedExpenseService) {
    @PostMapping
    @Operation(summary = "Criar despesa fixa")
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@Valid @RequestBody request: CreateFixedExpenseRequest): ResponseEntity<FixedExpenseResponse> =
        service.create(request.toEntity()).let { FixedExpenseResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    @Operation(summary = "Listar despesas fixas")
    fun list(): ResponseEntity<List<FixedExpenseResponse>> =
        service.findAll().map { FixedExpenseResponse(it) }
            .let { ResponseEntity.ok(it) }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar despesa fixa")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateFixedExpenseRequest
    ): ResponseEntity<FixedExpenseResponse> =
        service.update(id, request.toEntity()).let { FixedExpenseResponse(it) }
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover despesa fixa")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        service.delete(id).let { ResponseEntity.noContent().build() }
}
