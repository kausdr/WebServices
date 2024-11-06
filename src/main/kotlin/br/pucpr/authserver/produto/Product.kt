package br.pucpr.authserver.produto

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String,

    @NotNull
    val description: String,

    @NotNull
    val price: Double
)
