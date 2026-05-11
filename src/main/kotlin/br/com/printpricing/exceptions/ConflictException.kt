package br.com.printpricing.exceptions

import org.springframework.http.HttpStatus

class ConflictException(message: String) : DomainException(HttpStatus.CONFLICT, message)
