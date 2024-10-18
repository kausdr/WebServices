package br.pucpr.authserver.carrinho

import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.products.Product
import br.pucpr.authserver.products.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CarrinhoService(
    private val carrinhoRepository: CarrinhoRepository,
    private val productRepository: ProductRepository
) {
    @Transactional
    fun addProductToCart(cartId: Long, productId: Long): Carrinho {
        val cart = carrinhoRepository.findByIdOrNull(cartId)
            ?: throw NotFoundException("Carrinho com ID $cartId não encontrado!")

        val product = productRepository.findByIdOrNull(productId)
            ?: throw NotFoundException("Produto com ID $productId não encontrado!")

        cart.products.add(product)
        return carrinhoRepository.save(cart)
    }
}
