package br.com.printpricing.roles.services

import br.com.printpricing.roles.dtos.CreateRoleRequest
import br.com.printpricing.exceptions.BadRequestException
import br.com.printpricing.exceptions.ConflictException
import br.com.printpricing.roles.entities.Role
import br.com.printpricing.roles.repositories.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleService(private val repository: RoleRepository) {
    @Transactional
    fun create(request: CreateRoleRequest): Role {
        val normalizedName = request.name.trim().uppercase()
        if (repository.existsByNameIgnoreCase(normalizedName)) {
            throw ConflictException("Role '$normalizedName' ja existe")
        }

        return repository.save(
            Role(
                name = normalizedName,
                description = request.description.trim()
            )
        )
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Role> = repository.findAll().sortedBy { it.name }

    @Transactional(readOnly = true)
    fun ensureExists(roleName: String): Role {
        val normalized = roleName.trim().uppercase()
        return repository.findByNameIgnoreCase(normalized)
            .orElseThrow { BadRequestException("Role '$normalized' nao existe") }
    }
}
