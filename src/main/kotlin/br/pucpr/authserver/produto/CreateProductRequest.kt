package br.pucpr.authserver.products.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateProductRequest(
    @field:NotBlank(message = "O nome do produto é obrigatório")
    val name: String,

    @field:NotBlank(message = "A descrição do produto é obrigatória")
    val description: String,

    @field:Positive(message = "O preço deve ser maior que zero")
    val price: Double
)
