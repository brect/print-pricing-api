package br.com.printpricing.users.services

import br.com.printpricing.users.dtos.CreateUserRequest
import br.com.printpricing.users.dtos.UpdateUserRequest
import br.com.printpricing.users.dtos.UserResponse
import br.com.printpricing.exceptions.ConflictException
import br.com.printpricing.exceptions.ResourceNotFoundException
import br.com.printpricing.roles.services.RoleService
import br.com.printpricing.users.entities.User
import br.com.printpricing.users.mappers.UserMapper
import br.com.printpricing.users.repositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val roleService: RoleService,
    private val mapper: UserMapper
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
                active = true,
                roles = mutableSetOf(userRole)
            )
        )

        return mapper.toResponse(user)
    }

    fun toResponse(user: User): UserResponse = mapper.toResponse(user)

    @Transactional
    fun addRole(userId: Long, roleName: String): UserResponse {
        val user = repository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Usuario", userId) }

        val role = roleService.ensureExists(roleName)
        user.roles.add(role)
        return mapper.toResponse(repository.save(user))
    }

    @Transactional
    fun update(userId: Long, request: UpdateUserRequest): UserResponse {
        val user = repository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Usuario", userId) }

        request.email?.trim()?.lowercase()?.takeIf { it.isNotBlank() }?.let { email ->
            repository.findByEmailIgnoreCase(email).ifPresent { existing ->
                if (existing.id != user.id) {
                    throw ConflictException("Ja existe usuario com este e-mail")
                }
            }
            user.email = email
        }
        request.name?.trim()?.takeIf { it.isNotBlank() }?.let { user.name = it }
        request.description?.let { user.description = it.trim().ifBlank { null } }

        return mapper.toResponse(repository.save(user))
    }
}
