package br.com.printpricing.exceptions

import org.springframework.http.HttpStatus

class ResourceNotFoundException : DomainException {
    constructor(resource: String, id: Long) : super(HttpStatus.NOT_FOUND, "$resource com id $id nao encontrado")
    constructor(message: String) : super(HttpStatus.NOT_FOUND, message)
}
