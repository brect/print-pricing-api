package br.com.printpricing.marketplaces.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "marketplaces")
class Marketplace(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false)
    var active: Boolean = true,

    @OneToMany(mappedBy = "marketplace", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var feeRules: MutableList<MarketplaceFeeRule> = mutableListOf()
)
