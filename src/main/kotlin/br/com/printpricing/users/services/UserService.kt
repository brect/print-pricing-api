package br.com.printpricing.users.services

import br.com.printpricing.users.dtos.CreateUserRequest
import br.com.printpricing.users.dtos.LoginRequest
import br.com.printpricing.users.dtos.UserResponse
import br.com.printpricing.exceptions.ConflictException
import br.com.printpricing.exceptions.ResourceNotFoundException
import br.com.printpricing.roles.services.RoleService
import br.com.printpricing.users.entities.User
import br.com.printpricing.users.repositories.UserRepository
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val roleService: RoleService
) {
    @Transactional
    fun create(request: CreateUserRequest): UserResponse {
        if (repository.existsByEmailIgnoreCase(request.email)) {
            throw ConflictException("Ja existe usuario com este e-mail")
        }
        val userRole = roleService.ensureExists("USER")

        val user = repository.save(
            User(
                email = request.email.trim().lowercase(),
                password = requireNotNull(passwordEncoder.encode(request.password)) {
                    "Falha ao gerar hash da senha"
                },
                name = request.name.trim(),
                roles = mutableSetOf(userRole)
            )
        )

        return user.toUserResponse()
    }

    @Transactional(readOnly = true)
    fun authenticate(request: LoginRequest): User {
        val user = repository.findByEmailIgnoreCase(request.email.trim())
            .orElseThrow { BadCredentialsException("Credenciais invalidas") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BadCredentialsException("Credenciais invalidas")
        }

        return user
    }

    private fun User.toUserResponse() = UserResponse(
        id = id,
        email = email,
        name = name,
        roles = roles.map { it.name }.toSortedSet()
    )

    fun toResponse(user: User): UserResponse = user.toUserResponse()

    @Transactional
    fun addRole(userId: Long, roleName: String): UserResponse {
        val user = repository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Usuario", userId) }

        val role = roleService.ensureExists(roleName)
        user.roles.add(role)
        return repository.save(user).toUserResponse()
    }
}
