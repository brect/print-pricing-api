package br.com.printpricing.consumables.services

import br.com.printpricing.consumables.entities.Consumable
import br.com.printpricing.consumables.repositories.ConsumableRepository
import br.com.printpricing.exceptions.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConsumableService(private val repository: ConsumableRepository) {
    fun create(payload: Consumable): Consumable = repository.save(payload)
    fun findAll(): List<Consumable> = repository.findAll()
    fun findById(id: Long): Consumable =
        repository.findById(id).orElseThrow { ResourceNotFoundException("Consumivel", id) }

    @Transactional
    fun update(id: Long, payload: Consumable): Consumable {
        val current = findById(id)
        current.name = payload.name
        current.unitCost = payload.unitCost
        return repository.save(current)
    }

    @Transactional
    fun delete(id: Long) {
        repository.delete(findById(id))
    }
}
