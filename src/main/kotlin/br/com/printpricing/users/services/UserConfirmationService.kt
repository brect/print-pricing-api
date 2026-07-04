package br.com.printpricing.users.services

import br.com.printpricing.exceptions.BadRequestException
import br.com.printpricing.exceptions.ResourceNotFoundException
import br.com.printpricing.notifications.SmsSender
import br.com.printpricing.roles.services.RoleService
import br.com.printpricing.users.dtos.ConfirmUserRequest
import br.com.printpricing.users.dtos.UserResponse
import br.com.printpricing.users.entities.User
import br.com.printpricing.users.entities.UserConfirmationCode
import br.com.printpricing.users.mappers.UserMapper
import br.com.printpricing.users.repositories.UserConfirmationCodeRepository
import br.com.printpricing.users.repositories.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.LocalDateTime

@Service
class UserConfirmationService(
    private val users: UserRepository,
    private val confirmationCodes: UserConfirmationCodeRepository,
    private val roleService: RoleService,
    private val mapper: UserMapper,
    private val phoneNormalizer: PhoneNormalizer,
    private val smsSender: SmsSender,
    @Value("\${users.confirmation-code.expiration-minutes:5}")
    private val confirmationCodeExpirationMinutes: Long
) {
    private val random = SecureRandom()

    @Transactional
    fun requestConfirmation(phone: String, uuid: String) {
        val code = "%06d".format(random.nextInt(1_000_000))
        val expiresAt = LocalDateTime.now().plusMinutes(confirmationCodeExpirationMinutes)
        confirmationCodes.findByPhoneAndDeviceUuid(phone, uuid)
            .ifPresentOrElse(
                {
                    it.code = code
                    it.expiresAt = expiresAt
                    confirmationCodes.save(it)
                },
                {
                    confirmationCodes.save(
                        UserConfirmationCode(
                            phone = phone,
                            deviceUuid = uuid,
                            code = code,
                            expiresAt = expiresAt
                        )
                    )
                }
            )
        smsSender.send(phone, "codigo $code para uuid $uuid expira em $expiresAt")
    }

    @Transactional
    fun confirm(request: ConfirmUserRequest): UserResponse {
        val phone = phoneNormalizer.normalize(request.phone)
        val uuid = request.uuid.trim()
        val confirmationCode = confirmationCodes.findByPhoneAndDeviceUuid(phone, uuid)
            .orElseThrow { ResourceNotFoundException("Codigo de confirmacao para telefone/uuid informado") }

        if (confirmationCode.expiresAt.isBefore(LocalDateTime.now())) {
            confirmationCodes.delete(confirmationCode)
            throw BadRequestException("Codigo de confirmacao expirado")
        }

        if (confirmationCode.code != request.code.trim()) {
            throw BadRequestException("Codigo de confirmacao invalido")
        }

        val userRole = roleService.ensureExists("USER")
        val user = users.findByPhone(phone)
            .map {
                it.deviceUuid = uuid
                it.active = true
                it
            }
            .orElseGet {
                User(
                    phone = phone,
                    deviceUuid = uuid,
                    name = "Usuário Desconhecido",
                    active = true,
                    roles = mutableSetOf(userRole)
                )
            }

        confirmationCodes.delete(confirmationCode)
        return mapper.toResponse(users.save(user))
    }
}
