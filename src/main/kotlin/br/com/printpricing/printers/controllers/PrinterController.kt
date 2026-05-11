package br.com.printpricing.printers.controllers

import br.com.printpricing.printers.dtos.CreatePrinterRequest
import br.com.printpricing.printers.dtos.PrinterResponse
import br.com.printpricing.printers.services.PrinterService
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
@RequestMapping("/printers")
@Tag(name = "Printers", description = "Cadastro de impressoras 3D")
@SecurityRequirement(name = "jwt-auth")
class PrinterController(private val service: PrinterService) {
    @PostMapping
    @Operation(summary = "Criar impressora")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun create(@Valid @RequestBody request: CreatePrinterRequest): ResponseEntity<PrinterResponse> =
        service.create(request.toPrinter()).let { PrinterResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    @Operation(summary = "Listar impressoras")
    fun list(): ResponseEntity<List<PrinterResponse>> =
        service.findAll().map { PrinterResponse(it) }.let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar impressora por ID")
    fun getById(@PathVariable id: Long): ResponseEntity<PrinterResponse> =
        service.findById(id).let { PrinterResponse(it) }.let { ResponseEntity.ok(it) }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar impressora")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreatePrinterRequest
    ): ResponseEntity<PrinterResponse> =
        service.update(id, request.toPrinter()).let { PrinterResponse(it) }
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover impressora")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        service.delete(id).let { ResponseEntity.noContent().build() }
}
