package br.com.printpricing

import br.com.printpricing.marketplaces.entities.Marketplace
import br.com.printpricing.marketplaces.entities.MarketplaceFeeRule
import br.com.printpricing.marketplaces.entities.MarketplaceFeeType
import br.com.printpricing.marketplaces.repositories.MarketplaceRepository
import br.com.printpricing.materials.entities.FilamentMaterial
import br.com.printpricing.materials.repositories.FilamentMaterialRepository
import br.com.printpricing.printers.entities.Printer
import br.com.printpricing.printers.repositories.PrinterRepository
import br.com.printpricing.products.entities.Product
import br.com.printpricing.products.repositories.ProductRepository
import br.com.printpricing.roles.dtos.CreateRoleRequest
import br.com.printpricing.roles.services.RoleService
import br.com.printpricing.users.entities.User
import br.com.printpricing.users.repositories.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import java.math.BigDecimal

@Configuration
class Bootstrapper {
    @Bean
    fun seed(
        printers: PrinterRepository,
        materials: FilamentMaterialRepository,
        products: ProductRepository,
        marketplaces: MarketplaceRepository,
        users: UserRepository,
        roleService: RoleService,
        passwordEncoder: PasswordEncoder
    ) = CommandLineRunner {
        runCatching { roleService.create(CreateRoleRequest("USER", "Usuario padrao")) }
        runCatching { roleService.create(CreateRoleRequest("ADMIN", "Administrador do sistema")) }
        val userRole = roleService.ensureExists("USER")
        val adminRole = roleService.ensureExists("ADMIN")

        if (printers.count() == 0L) {
            printers.saveAll(
                listOf(
                    Printer(
                        name = "Ender 3 S1 Plus",
                        purchasePrice = BigDecimal("3800.00"),
                        maintenanceCost = BigDecimal("950.00"),
                        usefulLifeHours = 5000,
                        consumptionKw = BigDecimal("0.300")
                    ),
                    Printer(
                        name = "ANYCUBIC K3",
                        purchasePrice = BigDecimal("3900.00"),
                        maintenanceCost = BigDecimal("1000.00"),
                        usefulLifeHours = 5000,
                        consumptionKw = BigDecimal("0.350")
                    )
                )
            )
        }

        if (materials.count() == 0L) {
            materials.saveAll(
                listOf(
                    FilamentMaterial(
                        brand = "ABS Generico",
                        type = "ABS",
                        spoolCost = BigDecimal("80.00"),
                        spoolWeightKg = BigDecimal("1.000")
                    ),
                    FilamentMaterial(
                        brand = "PLA Generico",
                        type = "PLA",
                        spoolCost = BigDecimal("100.00"),
                        spoolWeightKg = BigDecimal("1.000")
                    ),
                    FilamentMaterial(
                        brand = "PETG VOOLT",
                        type = "PETG",
                        spoolCost = BigDecimal("110.00"),
                        spoolWeightKg = BigDecimal("1.000")
                    )
                )
            )
        }

        if (products.count() == 0L) {
            products.save(
                Product(
                    name = "LABUBU 100%",
                    sku = "LABUBU-100",
                    defaultWeightGrams = BigDecimal("36.00"),
                    defaultPrintMinutes = 143
                )
            )
        }

        if (marketplaces.count() == 0L) {
            marketplaces.saveAll(
                listOf(
                    marketplace("Shopee", BigDecimal("20.00")),
                    marketplace("Mercado Livre", BigDecimal("16.00")),
                    marketplace("TikTok Shop", BigDecimal("12.00"))
                )
            )
        }

        if (!users.existsByEmailIgnoreCase("admin@authserver.com")) {
            users.save(
                User(
                    email = "admin@authserver.com",
                    password = requireNotNull(passwordEncoder.encode("admin")) {
                        "Falha ao gerar hash da senha admin"
                    },
                    name = "Administrador",
                    roles = mutableSetOf(adminRole, userRole)
                )
            )
        }
    }

    private fun marketplace(name: String, percentage: BigDecimal): Marketplace =
        Marketplace(name = name).also { marketplace ->
            marketplace.feeRules.add(
                MarketplaceFeeRule(
                    marketplace = marketplace,
                    name = "Comissao padrao",
                    type = MarketplaceFeeType.PERCENTAGE,
                    percentage = percentage
                )
            )
        }
}
