package br.com.printpricing.expenses.repositories

import br.com.printpricing.expenses.entities.FixedAsset
import org.springframework.data.jpa.repository.JpaRepository

interface FixedAssetRepository : JpaRepository<FixedAsset, Long> {
    fun findByActiveTrue(): List<FixedAsset>
}
