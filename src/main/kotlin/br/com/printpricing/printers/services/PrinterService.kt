package br.com.printpricing.printers.services

import br.com.printpricing.exceptions.ResourceNotFoundException
import br.com.printpricing.printers.entities.Printer
import br.com.printpricing.printers.repositories.PrinterRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PrinterService(private val repository: PrinterRepository) {
    fun create(printer: Printer): Printer = repository.save(printer)
    fun findAll(): List<Printer> = repository.findAll()
    fun findById(id: Long): Printer =
        repository.findById(id).orElseThrow { ResourceNotFoundException("Impressora", id) }

    @Transactional
    fun update(id: Long, payload: Printer): Printer {
        val current = findById(id)
        current.name = payload.name
        current.purchasePrice = payload.purchasePrice
        current.maintenanceCost = payload.maintenanceCost
        current.usefulLifeHours = payload.usefulLifeHours
        current.consumptionKw = payload.consumptionKw
        return repository.save(current)
    }

    @Transactional
    fun delete(id: Long) {
        repository.delete(findById(id))
    }
}
