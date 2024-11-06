package br.pucpr.authserver.carrinho.responses

import br.pucpr.authserver.carrinho.Carrinho
import br.pucpr.authserver.produto.responses.ProductResponse

data class CarrinhoResponse(
    val id: Long?,
    val products: List<ProductResponse>
) {
    constructor(carrinho: Carrinho) : this(
        id = carrinho.id,
        products = carrinho.products.map { ProductResponse(it) }
    )
}