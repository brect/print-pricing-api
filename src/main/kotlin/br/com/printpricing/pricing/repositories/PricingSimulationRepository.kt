package br.com.printpricing.pricing.repositories

import br.com.printpricing.pricing.entities.PricingSimulation
import org.springframework.data.jpa.repository.JpaRepository

interface PricingSimulationRepository : JpaRepository<PricingSimulation, Long> {
    fun findByProductIdOrderByCreatedAtDesc(productId: Long): List<PricingSimulation>
}
