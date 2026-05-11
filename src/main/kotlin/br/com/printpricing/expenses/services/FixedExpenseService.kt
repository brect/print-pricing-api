package br.com.printpricing.expenses.services

import br.com.printpricing.expenses.entities.FixedExpense
import br.com.printpricing.expenses.repositories.FixedExpenseRepository
import br.com.printpricing.exceptions.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FixedExpenseService(private val repository: FixedExpenseRepository) {
    fun create(payload: FixedExpense): FixedExpense = repository.save(payload)
    fun findAll(): List<FixedExpense> = repository.findAll()
    fun findActive(): List<FixedExpense> = repository.findByActiveTrue()
    fun findById(id: Long): FixedExpense =
        repository.findById(id).orElseThrow { ResourceNotFoundException("Despesa fixa", id) }

    @Transactional
    fun update(id: Long, payload: FixedExpense): FixedExpense {
        val current = findById(id)
        current.name = payload.name
        current.monthlyAmount = payload.monthlyAmount
        current.allocationStrategy = payload.allocationStrategy
        current.active = payload.active
        current.monthlyUnitsCapacity = payload.monthlyUnitsCapacity
        current.monthlyMachineHoursCapacity = payload.monthlyMachineHoursCapacity
        return repository.save(current)
    }

    @Transactional
    fun delete(id: Long) {
        repository.delete(findById(id))
    }
}
