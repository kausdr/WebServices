package br.pucpr.authserver.carrinho

import br.pucpr.authserver.carrinho.responses.CarrinhoResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/carrinho")
class CarrinhoController(
    private val service: CarrinhoService
) {

    @PostMapping("/{cartId}/produto/{productId}")
    fun addProductToCart(
        @PathVariable cartId: Long,
        @PathVariable productId: Long
    ): ResponseEntity<CarrinhoResponse> {
        val updatedCart = service.addProductToCart(cartId, productId)
        return ResponseEntity.ok(CarrinhoResponse(updatedCart))
    }
}
