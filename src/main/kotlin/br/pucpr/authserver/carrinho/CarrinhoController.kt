package br.pucpr.authserver.carrinho

import br.pucpr.authserver.carrinho.responses.CarrinhoResponse
import br.pucpr.authserver.security.UserToken
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/carrinho")
class CarrinhoController(
    private val service: CarrinhoService,
) {

    @PostMapping
    @PreAuthorize("permitAll()")
    @SecurityRequirement(name="AuthServer")
    fun createCart(authentication: Authentication): ResponseEntity<CarrinhoResponse> {
        val userToken = authentication.principal as? UserToken
            ?: throw IllegalArgumentException("UserToken não encontrado na autenticação.")

        val userId = userToken.id
        val createdCart = service.createCartForUser(userId)
        return ResponseEntity.ok(CarrinhoResponse(createdCart))
    }

    @PostMapping("/produto/{productId}")
    @PreAuthorize("permitAll()")
    @SecurityRequirement(name="AuthServer")
    fun addProductToCart(
        authentication: Authentication,
        @PathVariable productId: Long
    ): ResponseEntity<CarrinhoResponse> {
        val userToken = authentication.principal as? UserToken
            ?: throw IllegalArgumentException("UserToken não encontrado na autenticação.")

        val userId = userToken.id
        val updatedCart = service.addProductToCart(userId, productId)
        return ResponseEntity.ok(CarrinhoResponse(updatedCart))
    }

    @DeleteMapping("/produto/{productId}")
    @PreAuthorize("permitAll()")
    @SecurityRequirement(name="AuthServer")
    fun removeProductFromCart(
        authentication: Authentication,
        @PathVariable productId: Long
    ): ResponseEntity<CarrinhoResponse> {
        val userToken = authentication.principal as? UserToken
            ?: throw IllegalArgumentException("UserToken não encontrado na autenticação.")

        val userId = userToken.id
        val updatedCart = service.removeProductFromCart(userId, productId)
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
    @PreAuthorize("hasRole('ADMIN')")
    fun findCartById(@PathVariable id: Long): ResponseEntity<CarrinhoResponse> {
        val cart = service.findById(id)
        return ResponseEntity.ok(CarrinhoResponse(cart))
    }

    @GetMapping("/meu-carrinho")
    @PreAuthorize("permitAll()")
    @SecurityRequirement(name="AuthServer")
    fun getOwnCart(authentication: Authentication): ResponseEntity<CarrinhoResponse> {
        val userToken = authentication.principal as? UserToken
            ?: throw IllegalArgumentException("UserToken não encontrado na autenticação.")

        val userId = userToken.id
        val cart = service.findCartByUserId(userId)
        return ResponseEntity.ok(CarrinhoResponse(cart))
    }

    @DeleteMapping
    @PreAuthorize("permitAll()")
    @SecurityRequirement(name="AuthServer")
    fun deleteCart(authentication: Authentication): ResponseEntity<Void> {
        val userToken = authentication.principal as? UserToken
            ?: throw IllegalArgumentException("UserToken não encontrado na autenticação.")

        val userId = userToken.id
        service.deleteCartForUser(userId)
        return ResponseEntity.noContent().build()
    }
}