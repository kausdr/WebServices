package br.pucpr.authserver.carrinho

import br.pucpr.authserver.produto.Product
import jakarta.persistence.*

@Entity
data class Carrinho(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "CarrinhoProduct",
        joinColumns = [JoinColumn(name = "idCarrinho")],
        inverseJoinColumns = [JoinColumn(name = "idProduct")]
    )
    val products: MutableList<Product> = mutableListOf()
)
