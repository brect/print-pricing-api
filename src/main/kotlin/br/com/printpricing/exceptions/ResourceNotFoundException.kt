package br.com.printpricing.exceptions

import org.springframework.http.HttpStatus

class ResourceNotFoundException(resource: String, id: Long) :
    DomainException(HttpStatus.NOT_FOUND, "$resource com id $id nao encontrado")
