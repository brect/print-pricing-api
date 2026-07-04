package br.com.printpricing.users.mappers

import br.com.printpricing.users.dtos.UserResponse
import br.com.printpricing.users.entities.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun toResponse(user: User) = UserResponse(
        id = user.id,
        email = user.email,
        phone = user.phone,
        name = user.name,
        description = user.description,
        active = user.active,
        roles = user.roles.map { it.name }.toSortedSet()
    )
}
