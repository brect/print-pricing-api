package br.com.printpricing.consumables.controllers

import br.com.printpricing.consumables.dtos.ConsumableResponse
import br.com.printpricing.consumables.dtos.CreateConsumableRequest
import br.com.printpricing.consumables.services.ConsumableService
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
@RequestMapping("/consumables")
@Tag(name = "Consumables", description = "Catalogo de consumiveis")
@SecurityRequirement(name = "jwt-auth")
class ConsumableController(private val service: ConsumableService) {
    @PostMapping
    @Operation(summary = "Criar consumivel")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun create(@Valid @RequestBody request: CreateConsumableRequest): ResponseEntity<ConsumableResponse> =
        service.create(request.toEntity()).let { ConsumableResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    @Operation(summary = "Listar consumiveis")
    fun list(): ResponseEntity<List<ConsumableResponse>> =
        service.findAll().map { ConsumableResponse(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar consumivel por ID")
    fun getById(@PathVariable id: Long): ResponseEntity<ConsumableResponse> =
        service.findById(id).let { ConsumableResponse(it) }
            .let { ResponseEntity.ok(it) }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar consumivel")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateConsumableRequest
    ): ResponseEntity<ConsumableResponse> =
        service.update(id, request.toEntity()).let { ConsumableResponse(it) }
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover consumivel")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        service.delete(id).let { ResponseEntity.noContent().build() }
}
