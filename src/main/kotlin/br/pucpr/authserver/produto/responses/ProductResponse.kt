package br.pucpr.authserver.produto.responses

import br.pucpr.authserver.produto.Product

data class ProductResponse(
    val id: Long?,
    val name: String,
    val description: String,
    val price: Double
) {
    constructor(product: Product) : this(
        id = product.id!!,
        name = product.name,
        description = product.description,
        price = product.price
    )
}
