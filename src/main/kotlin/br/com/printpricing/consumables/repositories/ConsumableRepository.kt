package br.com.printpricing.consumables.repositories

import br.com.printpricing.consumables.entities.Consumable
import org.springframework.data.jpa.repository.JpaRepository

interface ConsumableRepository : JpaRepository<Consumable, Long>
