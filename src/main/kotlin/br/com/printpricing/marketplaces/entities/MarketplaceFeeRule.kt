package br.com.printpricing.marketplaces.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

enum class MarketplaceFeeType {
    PERCENTAGE,
    FIXED_AMOUNT,
    PERCENTAGE_PLUS_FIXED
}

@Entity
@Table(name = "marketplace_fee_rules")
class MarketplaceFeeRule(
    @Id @GeneratedValue
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_id", nullable = false)
    var marketplace: Marketplace,

    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: MarketplaceFeeType,

    @Column(nullable = false, precision = 8, scale = 4)
    var percentage: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 12, scale = 2)
    var fixedAmount: BigDecimal = BigDecimal.ZERO
)
