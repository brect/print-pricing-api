package br.com.printpricing.expenses.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "fixed_expenses")
class FixedExpense(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, precision = 12, scale = 2)
    var monthlyAmount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var allocationStrategy: ExpenseAllocationStrategy = ExpenseAllocationStrategy.PER_MACHINE_HOUR,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(nullable = false)
    var monthlyUnitsCapacity: Int = 100,

    @Column(nullable = false, precision = 10, scale = 2)
    var monthlyMachineHoursCapacity: BigDecimal = BigDecimal("160.00")
)
