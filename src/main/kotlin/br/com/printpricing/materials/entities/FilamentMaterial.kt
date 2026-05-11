package br.com.printpricing.materials.entities

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
@Table(name = "filament_materials")
class FilamentMaterial(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var brand: String,

    @Column(nullable = false)
    var type: String,

    @Column(nullable = false, precision = 12, scale = 2)
    var spoolCost: BigDecimal,

    @Column(nullable = false, precision = 8, scale = 3)
    var spoolWeightKg: BigDecimal = BigDecimal.ONE,

    @Column
    var color: String? = null,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
) {
    fun costPerKg(): BigDecimal =
        spoolCost.divide(spoolWeightKg, 4, RoundingMode.HALF_UP)
}
