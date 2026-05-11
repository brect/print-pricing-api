package br.com.printpricing.categories.controllers

import br.com.printpricing.categories.dtos.CategoryResponse
import br.com.printpricing.categories.dtos.CreateCategoryRequest
import br.com.printpricing.categories.services.CategoryService
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
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Categorias de produtos")
@SecurityRequirement(name = "jwt-auth")
class CategoryController(private val service: CategoryService) {
    @PostMapping
    @Operation(summary = "Criar categoria")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun create(@Valid @RequestBody request: CreateCategoryRequest): ResponseEntity<CategoryResponse> =
        service.create(request.toEntity()).let { CategoryResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    @Operation(summary = "Listar categorias")
    fun list(): ResponseEntity<List<CategoryResponse>> =
        service.findAll().map { CategoryResponse(it) }.let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID")
    fun getById(@PathVariable id: Long): ResponseEntity<CategoryResponse> =
        service.findById(id).let { CategoryResponse(it) }.let { ResponseEntity.ok(it) }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateCategoryRequest
    ): ResponseEntity<CategoryResponse> =
        service.update(id, request.toEntity()).let { CategoryResponse(it) }.let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover categoria")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        service.delete(id).let { ResponseEntity.noContent().build() }
}
