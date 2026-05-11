package br.com.printpricing.roles.repositories

import br.com.printpricing.roles.entities.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RoleRepository : JpaRepository<Role, Long> {
    fun findByNameIgnoreCase(name: String): Optional<Role>
    fun existsByNameIgnoreCase(name: String): Boolean
}
