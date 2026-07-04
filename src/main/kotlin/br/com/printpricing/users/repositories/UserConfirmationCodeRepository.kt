package br.com.printpricing.users.repositories

import br.com.printpricing.users.entities.UserConfirmationCode
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserConfirmationCodeRepository : JpaRepository<UserConfirmationCode, Long> {
    fun findByPhoneAndDeviceUuid(phone: String, deviceUuid: String): Optional<UserConfirmationCode>
    fun deleteByPhoneAndDeviceUuid(phone: String, deviceUuid: String)
}
