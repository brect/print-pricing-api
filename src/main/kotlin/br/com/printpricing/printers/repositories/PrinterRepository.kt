package br.com.printpricing.printers.repositories

import br.com.printpricing.printers.entities.Printer
import org.springframework.data.jpa.repository.JpaRepository

interface PrinterRepository : JpaRepository<Printer, Long>
