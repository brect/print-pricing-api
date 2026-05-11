package br.com.printpricing.marketplaces.controllers

import br.com.printpricing.marketplaces.dtos.CreateMarketplaceRequest
import br.com.printpricing.marketplaces.dtos.MarketplaceResponse
import br.com.printpricing.marketplaces.services.MarketplaceService
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
@RequestMapping("/marketplaces")
@Tag(name = "Marketplaces", description = "Regras de taxas por canal")
@SecurityRequirement(name = "jwt-auth")
class MarketplaceController(private val service: MarketplaceService) {
    @PostMapping
    @Operation(summary = "Criar marketplace")
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@Valid @RequestBody request: CreateMarketplaceRequest): ResponseEntity<MarketplaceResponse> =
        service.create(request.toMarketplace()).let { MarketplaceResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    @Operation(summary = "Listar marketplaces")
    fun list(): ResponseEntity<List<MarketplaceResponse>> =
        service.findAll().map { MarketplaceResponse(it) }.let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar marketplace por ID")
    fun getById(@PathVariable id: Long): ResponseEntity<MarketplaceResponse> =
        service.findById(id).let { MarketplaceResponse(it) }.let { ResponseEntity.ok(it) }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar marketplace")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateMarketplaceRequest
    ): ResponseEntity<MarketplaceResponse> =
        service.update(id, request.toMarketplace()).let { MarketplaceResponse(it) }
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar marketplace")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        service.delete(id).let { ResponseEntity.noContent().build() }
}
