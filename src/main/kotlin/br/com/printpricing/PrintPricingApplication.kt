package br.com.printpricing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PrintPricingApplication

fun main(args: Array<String>) {
    runApplication<PrintPricingApplication>(*args)
}
