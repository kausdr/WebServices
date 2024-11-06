package br.pucpr.authserver.carrinho

import br.pucpr.authserver.produto.Product
import jakarta.persistence.*

@Entity
data class Carrinho(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val products: MutableList<Product> = mutableListOf()
)
