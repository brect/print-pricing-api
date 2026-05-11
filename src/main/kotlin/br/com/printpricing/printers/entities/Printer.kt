package br.com.printpricing.printers.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Entity
@Table(name = "printers")
class Printer(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, precision = 12, scale = 2)
    var purchasePrice: BigDecimal,

    @Column(nullable = false, precision = 12, scale = 2)
    var maintenanceCost: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    var usefulLifeHours: Int,

    @Column(nullable = false, precision = 8, scale = 3)
    var consumptionKw: BigDecimal,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
) {
    fun depreciationPerHour(): BigDecimal =
        purchasePrice.add(maintenanceCost)
            .divide(BigDecimal(usefulLifeHours), 4, RoundingMode.HALF_UP)
}
