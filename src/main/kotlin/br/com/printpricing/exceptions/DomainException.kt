package br.com.printpricing.exceptions

import org.springframework.http.HttpStatus

open class DomainException(
    val status: HttpStatus,
    override val message: String
) : RuntimeException(message)
