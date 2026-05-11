package br.com.printpricing.exceptions

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(DomainException::class)
    fun domain(ex: DomainException, request: HttpServletRequest): ResponseEntity<ApiError> =
        ResponseEntity.status(ex.status).body(
            ApiError(
                status = ex.status.value(),
                error = ex.status.reasonPhrase,
                message = ex.message,
                path = request.requestURI
            )
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validation(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val details = ex.bindingResult.fieldErrors
            .associate { it.field to (it.defaultMessage ?: "valor invalido") }
        return ResponseEntity.badRequest().body(
            ApiError(
                status = 400,
                error = "Bad Request",
                message = "Erro de validacao",
                path = request.requestURI,
                details = details
            )
        )
    }

    @ExceptionHandler(BadCredentialsException::class, AccessDeniedException::class)
    fun security(ex: Exception, request: HttpServletRequest): ResponseEntity<ApiError> {
        val (status, error, message) = when (ex) {
            is BadCredentialsException -> Triple(401, "Unauthorized", ex.message ?: "Nao autenticado")
            else -> Triple(403, "Forbidden", "Acesso negado")
        }
        return ResponseEntity.status(status).body(
            ApiError(
                status = status,
                error = error,
                message = message,
                path = request.requestURI
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun unexpected(ex: Exception, request: HttpServletRequest): ResponseEntity<ApiError> =
        ResponseEntity.internalServerError().body(
            ApiError(
                status = 500,
                error = "Internal Server Error",
                message = "Erro inesperado ao processar a requisicao",
                path = request.requestURI
            )
        )
}
