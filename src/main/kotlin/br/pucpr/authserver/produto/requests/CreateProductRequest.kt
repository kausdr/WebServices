package br.pucpr.authserver.produto.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateProductRequest(
    @field:NotBlank(message = "Preencha aqui o nome do produto")
    val name: String,

    @field:NotBlank(message = "Preencha aqui a descrição do produto")
    val description: String,

    @field:Positive
    @field:NotBlank(message = "Preencha aqui o preço do produto")
    val price: Double
)
