package br.pucpr.authserver.carrinho.responses

import br.pucpr.authserver.carrinho.Carrinho
import br.pucpr.authserver.produto.responses.ProductResponse

data class CarrinhoResponse(
    val id: Long?,
    val nome: String,
    val products: List<ProductResponse>
) {
    constructor(carrinho: Carrinho) : this(
        id = carrinho.id,
        nome = carrinho.nome,
        products = carrinho.products.map { ProductResponse(it) }
    )
}