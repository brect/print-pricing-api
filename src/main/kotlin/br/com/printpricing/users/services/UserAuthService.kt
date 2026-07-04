package br.com.printpricing.users.services

import br.com.printpricing.users.dtos.LoginRequest
import br.com.printpricing.users.entities.User
import br.com.printpricing.users.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAuthService(
    private val users: UserRepository,
    private val confirmationService: UserConfirmationService,
    private val phoneNormalizer: PhoneNormalizer
) {
    @Transactional
    fun authenticate(request: LoginRequest): LoginResult {
        val phone = phoneNormalizer.normalize(request.phone)
        val uuid = request.uuid.trim()

        val existingUser = users.findByPhoneAndDeviceUuidAndActiveTrue(phone, uuid)
        if (existingUser.isPresent) {
            return LoginResult.Authenticated(existingUser.get())
        }

        confirmationService.requestConfirmation(phone, uuid)
        return LoginResult.ConfirmationRequired
    }
}

sealed class LoginResult {
    data class Authenticated(val user: User) : LoginResult()
    data object ConfirmationRequired : LoginResult()
}
