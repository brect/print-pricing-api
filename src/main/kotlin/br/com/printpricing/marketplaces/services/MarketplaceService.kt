package br.com.printpricing.marketplaces.services

import br.com.printpricing.exceptions.ResourceNotFoundException
import br.com.printpricing.marketplaces.entities.Marketplace
import br.com.printpricing.marketplaces.entities.MarketplaceFeeRule
import br.com.printpricing.marketplaces.repositories.MarketplaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MarketplaceService(private val repository: MarketplaceRepository) {
    fun create(marketplace: Marketplace): Marketplace = repository.save(marketplace)
    fun findAll(): List<Marketplace> = repository.findAll()
    fun findActive(): List<Marketplace> = repository.findByActiveTrue()
    fun findById(id: Long): Marketplace =
        repository.findById(id).orElseThrow { ResourceNotFoundException("Marketplace", id) }

    @Transactional
    fun update(id: Long, payload: Marketplace): Marketplace {
        val current = findById(id)
        current.name = payload.name
        current.active = payload.active

        current.feeRules.clear()
        current.feeRules.addAll(
            payload.feeRules.map {
                MarketplaceFeeRule(
                    marketplace = current,
                    name = it.name,
                    type = it.type,
                    percentage = it.percentage,
                    fixedAmount = it.fixedAmount
                )
            }
        )

        return repository.save(current)
    }

    @Transactional
    fun delete(id: Long) {
        val current = findById(id)
        repository.delete(current)
    }
}
