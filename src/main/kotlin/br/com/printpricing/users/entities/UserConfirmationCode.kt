package br.com.printpricing.users.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user_confirmation_codes")
class UserConfirmationCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var phone: String,

    @Column(name = "device_uuid", nullable = false)
    var deviceUuid: String,

    @Column(nullable = false)
    var code: String,

    @Column(nullable = false)
    var expiresAt: LocalDateTime,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
)
