package br.com.printpricing.users.repositories

import br.com.printpricing.users.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmailIgnoreCase(email: String): Optional<User>
    fun existsByEmailIgnoreCase(email: String): Boolean
    fun findByPhone(phone: String): Optional<User>
    fun findByPhoneAndDeviceUuidAndActiveTrue(phone: String, deviceUuid: String): Optional<User>
}
