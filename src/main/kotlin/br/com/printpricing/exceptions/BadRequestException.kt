package br.com.printpricing.exceptions

import org.springframework.http.HttpStatus

class BadRequestException(message: String) : DomainException(HttpStatus.BAD_REQUEST, message)
