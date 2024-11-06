package br.pucpr.authserver.carrinho

import br.pucpr.authserver.carrinho.responses.CarrinhoResponse
import br.pucpr.authserver.users.UserService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/carrinho")
class CarrinhoController(
    private val service: CarrinhoService,
    private val userService: UserService
) {

    @PostMapping
    fun createCart(authentication: Authentication): ResponseEntity<CarrinhoResponse> {
        val user = userService.findByEmail(authentication.name)
        val createdCart = service.createCartForUser(user)
        return ResponseEntity.ok(CarrinhoResponse(createdCart))
    }

    @PostMapping("/produto/{productId}")
    fun addProductToCart(
        authentication: Authentication,
        @PathVariable productId: Long
    ): ResponseEntity<CarrinhoResponse> {
        val user = userService.findByEmail(authentication.name)
        val updatedCart = service.addProductToCart(user, productId)
        return ResponseEntity.ok(CarrinhoResponse(updatedCart))
    }

    @DeleteMapping("/produto/{productId}")
    fun removeProductFromCart(
        authentication: Authentication,
        @PathVariable productId: Long
    ): ResponseEntity<CarrinhoResponse> {
        val user = userService.findByEmail(authentication.name)
        val updatedCart = service.removeProductFromCart(user, productId)
        return ResponseEntity.ok(CarrinhoResponse(updatedCart))
    }

    @GetMapping
    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    fun listCarts(): ResponseEntity<List<CarrinhoResponse>> {
        val carts = service.listCarts().map { CarrinhoResponse(it) }
        return ResponseEntity.ok(carts)
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("permitAll()")
    fun findCartById(@PathVariable id: Long): ResponseEntity<CarrinhoResponse> {
        val cart = service.findById(id)
        return ResponseEntity.ok(CarrinhoResponse(cart))
    }

    @DeleteMapping
    fun deleteCart(authentication: Authentication): ResponseEntity<Void> {
        val user = userService.findByEmail(authentication.name)
        service.deleteCartForUser(user)
        return ResponseEntity.noContent().build()
    }
}
