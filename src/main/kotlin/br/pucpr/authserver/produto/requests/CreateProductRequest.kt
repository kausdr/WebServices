package br.pucpr.authserver.produto.requests

import br.pucpr.authserver.produto.Product
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateProductRequest(
    @field:NotBlank(message = "Preencha aqui o nome do produto")
    val name: String? = null,

    @field:NotBlank(message = "Preencha aqui a descrição do produto")
    val description: String? = null,

    @field:Positive(message = "Preencha aqui o preço do produto")
    val price: Double? = null
) {
    fun toProduct() = Product(
        name = name!!,
        description = description!!,
        price = price!!
    )
}
