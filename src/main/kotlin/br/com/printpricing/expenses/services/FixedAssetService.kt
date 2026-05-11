package br.com.printpricing.expenses.services

import br.com.printpricing.expenses.entities.FixedAsset
import br.com.printpricing.expenses.repositories.FixedAssetRepository
import br.com.printpricing.exceptions.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FixedAssetService(private val repository: FixedAssetRepository) {
    fun create(payload: FixedAsset): FixedAsset = repository.save(payload)
    fun findAll(): List<FixedAsset> = repository.findAll()
    fun findActive(): List<FixedAsset> = repository.findByActiveTrue()
    fun findById(id: Long): FixedAsset =
        repository.findById(id).orElseThrow { ResourceNotFoundException("Ativo fixo", id) }

    @Transactional
    fun update(id: Long, payload: FixedAsset): FixedAsset {
        val current = findById(id)
        current.name = payload.name
        current.cost = payload.cost
        current.usefulLifeMonths = payload.usefulLifeMonths
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
