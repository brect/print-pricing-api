package br.com.printpricing.expenses.repositories

import br.com.printpricing.expenses.entities.FixedExpense
import org.springframework.data.jpa.repository.JpaRepository

interface FixedExpenseRepository : JpaRepository<FixedExpense, Long> {
    fun findByActiveTrue(): List<FixedExpense>
}
