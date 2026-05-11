package br.com.printpricing.marketplaces.repositories

import br.com.printpricing.marketplaces.entities.Marketplace
import org.springframework.data.jpa.repository.JpaRepository

interface MarketplaceRepository : JpaRepository<Marketplace, Long> {
    fun findByActiveTrue(): List<Marketplace>
}
