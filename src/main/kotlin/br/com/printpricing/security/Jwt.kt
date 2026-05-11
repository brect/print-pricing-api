package br.com.printpricing.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import tools.jackson.databind.ObjectMapper

@Component
class Jwt(
    @Value("\${security.jwt.issuer:print-pricing-api}")
    private val issuer: String,
    @Value("\${security.jwt.secret:print-pricing-secret-key-change-in-production-32chars}")
    private val secret: String,
    private val mapper: ObjectMapper
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun create(userToken: UserToken): String {
        val now = Instant.now()
        val expiration = if (userToken.isAdmin) now.plus(1, ChronoUnit.HOURS) else now.plus(48, ChronoUnit.HOURS)

        return Jwts.builder()
            .issuer(issuer)
            .subject(userToken.id.toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .claim("user", userToken)
            .signWith(key)
            .compact()
    }

    fun extract(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val header = request.getHeader("Authorization") ?: return null
        if (!header.startsWith("Bearer ")) return null

        val token = header.removePrefix("Bearer ").trim()
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        val userToken = mapper.convertValue(claims["user"], UserToken::class.java)
        val authorities = userToken.roles.map { SimpleGrantedAuthority("ROLE_$it") }

        return UsernamePasswordAuthenticationToken(userToken, null, authorities)
    }

    fun authenticatedUser(): UserToken? =
        SecurityContextHolder.getContext().authentication?.principal as? UserToken
}
