package br.com.printpricing.products.controllers

import br.com.printpricing.products.dtos.CreateProductRequest
import br.com.printpricing.products.dtos.ProductResponse
import br.com.printpricing.products.services.ProductService
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
@RequestMapping("/products")
@Tag(name = "Products", description = "Produtos/impressões para precificação")
@SecurityRequirement(name = "jwt-auth")
class ProductController(private val service: ProductService) {
    @PostMapping
    @Operation(summary = "Criar produto")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun create(@Valid @RequestBody request: CreateProductRequest): ResponseEntity<ProductResponse> =
        service.create(request.toProduct()).let { ProductResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    @Operation(summary = "Listar produtos")
    fun list(): ResponseEntity<List<ProductResponse>> =
        service.findAll().map { ProductResponse(it) }.let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    fun getById(@PathVariable id: Long): ResponseEntity<ProductResponse> =
        service.findById(id).let { ProductResponse(it) }.let { ResponseEntity.ok(it) }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateProductRequest
    ): ResponseEntity<ProductResponse> =
        service.update(id, request.toProduct()).let { ProductResponse(it) }
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover produto")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        service.delete(id).let { ResponseEntity.noContent().build() }
}
