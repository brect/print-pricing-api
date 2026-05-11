package br.com.printpricing.pricing.controllers

import br.com.printpricing.pricing.dtos.CreatePricingSimulationRequest
import br.com.printpricing.pricing.dtos.PricingSimulationResponse
import br.com.printpricing.pricing.services.PricingSimulationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pricing/simulations")
@Tag(name = "Pricing", description = "Simulacoes de custo e preco")
@SecurityRequirement(name = "jwt-auth")
class PricingSimulationController(private val service: PricingSimulationService) {
    @PostMapping
    @Operation(summary = "Criar simulacao de custo e preco de venda")
    fun simulate(@Valid @RequestBody request: CreatePricingSimulationRequest): ResponseEntity<PricingSimulationResponse> =
        service.simulate(request).let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar simulacao por ID")
    fun getById(@PathVariable id: Long): ResponseEntity<PricingSimulationResponse> =
        service.findById(id).let { ResponseEntity.ok(it) }

    @GetMapping
    @Operation(summary = "Listar simulacoes por produto")
    fun list(
        @RequestParam(required = false) productId: Long?
    ): ResponseEntity<List<PricingSimulationResponse>> =
        when {
            productId != null -> service.findByProduct(productId)
            else -> emptyList()
        }.let { ResponseEntity.ok(it) }
}
