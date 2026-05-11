package br.com.printpricing.materials.controllers

import br.com.printpricing.materials.dtos.CreateMaterialRequest
import br.com.printpricing.materials.dtos.MaterialResponse
import br.com.printpricing.materials.services.FilamentMaterialService
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
@RequestMapping("/materials")
@Tag(name = "Materials", description = "Cadastro de filamentos")
@SecurityRequirement(name = "jwt-auth")
class FilamentMaterialController(private val service: FilamentMaterialService) {
    @PostMapping
    @Operation(summary = "Criar material")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun create(@Valid @RequestBody request: CreateMaterialRequest): ResponseEntity<MaterialResponse> =
        service.create(request.toMaterial()).let { MaterialResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    @Operation(summary = "Listar materiais")
    fun list(): ResponseEntity<List<MaterialResponse>> =
        service.findAll().map { MaterialResponse(it) }.let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar material por ID")
    fun getById(@PathVariable id: Long): ResponseEntity<MaterialResponse> =
        service.findById(id).let { MaterialResponse(it) }.let { ResponseEntity.ok(it) }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar material")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateMaterialRequest
    ): ResponseEntity<MaterialResponse> =
        service.update(id, request.toMaterial()).let { MaterialResponse(it) }
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover material")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        service.delete(id).let { ResponseEntity.noContent().build() }
}
