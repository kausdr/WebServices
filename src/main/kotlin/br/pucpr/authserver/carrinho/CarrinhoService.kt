package br.pucpr.authserver.carrinho

import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.produto.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CarrinhoService(
    private val carrinhoRepository: CarrinhoRepository,
    private val productRepository: ProductRepository
) {

    @Transactional
    fun createCartForUser(user: Long): Carrinho {
        if (carrinhoRepository.findByUserId(user) != null) {
            log.warn("Tentativa de criação de novo carrinho para usuário com ID $user, que já possui um carrinho.")
            throw IllegalStateException("O usuário já possui um carrinho ativo.")
        }

        val carrinhoCount = carrinhoRepository.count() + 1
        val nomeCarrinho = "carrinho${carrinhoCount}"
        log.info("Criando novo carrinho para o usuário com ID $user.")

        val novoCarrinho = Carrinho(userId = user, nome = nomeCarrinho)
        log.info("Carrinho criado com sucesso para o usuário com ID $user. ID do carrinho: ${novoCarrinho.id}")
        return carrinhoRepository.save(novoCarrinho)
    }

    @Transactional
    fun addProductToCart(user: Long, productId: Long): Carrinho {
        val cart = carrinhoRepository.findByUserId(user)
            ?: throw NotFoundException("Carrinho não encontrado para o usuário com ID $user")

        val product = productRepository.findByIdOrNull(productId)
            ?: throw NotFoundException("Produto com ID $productId não encontrado!")

        if (!cart.products.contains(product)) {
            cart.products.add(product)
            log.info("Produto com ID $productId adicionado ao carrinho do usuário com ID ${user}.")
        } else {
            log.warn("Produto com ID $productId já está no carrinho do usuário com ID ${user}.")
            throw IllegalStateException("Produto já está no carrinho.")
        }

        return carrinhoRepository.save(cart)
    }

    @Transactional
    fun removeProductFromCart(user: Long, productId: Long): Carrinho {
        val cart = carrinhoRepository.findByUserId(user)
            ?: throw NotFoundException("Carrinho não encontrado para o usuário com ID $user")

        val productRemoved = cart.products.removeIf { it.id == productId }

        if (productRemoved) {
            log.info("Produto com ID $productId removido do carrinho do usuário com ID ${user}.")
        } else {
            log.warn("Produto com ID $productId não encontrado no carrinho do usuário com ID ${user}.")
            throw NotFoundException("Produto com ID $productId não encontrado no carrinho.")
        }

        return carrinhoRepository.save(cart)
    }

    fun listCarts(): List<Carrinho> {
        log.info("Listando todos os carrinhos do servidor.")
        return carrinhoRepository.findAll()
    }

    fun findById(id: Long): Carrinho {
        log.info("Buscando carrinho com ID $id.")
        return carrinhoRepository.findByIdOrNull(id)
            ?: throw NotFoundException("Carrinho com ID $id não encontrado!")
    }

    fun findCartByUserId(userId: Long): Carrinho {
        return carrinhoRepository.findByUserId(userId)
            ?: throw NotFoundException("Carrinho não encontrado para o usuário com ID $userId.")
    }

    fun deleteCartForUser(user: Long) {
        val cart = carrinhoRepository.findByUserId(user)
            ?: throw NotFoundException("Carrinho não encontrado para o usuário com ID $user.")

        cart.products.clear()
        carrinhoRepository.delete(cart)
        log.info("Carrinho do usuário com ID $user deletado com sucesso.")
    }

    companion object {
        private val log = LoggerFactory.getLogger(CarrinhoService::class.java)
    }
}