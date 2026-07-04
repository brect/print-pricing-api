package br.com.printpricing.users.services

import br.com.printpricing.exceptions.BadRequestException
import org.springframework.stereotype.Component

@Component
class PhoneNormalizer {
    fun normalize(phone: String): String =
        phone.trim().filter { it.isDigit() || it == '+' }
            .ifBlank { throw BadRequestException("Telefone invalido") }
}
