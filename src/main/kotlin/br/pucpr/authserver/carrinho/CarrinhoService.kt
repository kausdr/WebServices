package br.pucpr.authserver.carrinho

import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.produto.ProductRepository
import br.pucpr.authserver.users.User
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
    fun createCartForUser(user: User): Carrinho {
        val userId = user.id ?: throw IllegalArgumentException("ID do usuário é obrigatório")

        if (carrinhoRepository.findByUserId(userId) != null) {
            log.warn("Tentativa de criação de novo carrinho para usuário com ID $userId, que já possui um carrinho.")
            throw IllegalStateException("O usuário já possui um carrinho ativo.")
        }

        val novoCarrinho = Carrinho(userId = userId)
        log.info("Criando novo carrinho para o usuário com ID $userId.")

        val savedCart = carrinhoRepository.save(novoCarrinho)
        log.info("Carrinho criado com sucesso para o usuário com ID $userId. ID do carrinho: ${savedCart.id}")
        return savedCart
    }

    @Transactional
    fun addProductToCart(user: User, productId: Long): Carrinho {
        val cart = carrinhoRepository.findByUserId(user.id!!)
            ?: throw NotFoundException("Carrinho não encontrado para o usuário com ID ${user.id}")

        val product = productRepository.findByIdOrNull(productId)
            ?: throw NotFoundException("Produto com ID $productId não encontrado!")

        if (!cart.products.contains(product)) {
            cart.products.add(product)
            log.info("Produto com ID $productId adicionado ao carrinho do usuário com ID ${user.id}.")
        } else {
            log.warn("Produto com ID $productId já está no carrinho do usuário com ID ${user.id}.")
            throw IllegalStateException("Produto já está no carrinho.")
        }

        return carrinhoRepository.save(cart)
    }

    @Transactional
    fun removeProductFromCart(user: User, productId: Long): Carrinho {
        val cart = carrinhoRepository.findByUserId(user.id!!)
            ?: throw NotFoundException("Carrinho não encontrado para o usuário com ID ${user.id}")

        val productRemoved = cart.products.removeIf { it.id == productId }

        if (productRemoved) {
            log.info("Produto com ID $productId removido do carrinho do usuário com ID ${user.id}.")
        } else {
            log.warn("Produto com ID $productId não encontrado no carrinho do usuário com ID ${user.id}.")
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

    fun deleteCartForUser(user: User) {
        val userId = user.id ?: throw IllegalArgumentException("ID do usuário é obrigatório")
        val cart = carrinhoRepository.findByUserId(userId)
            ?: throw NotFoundException("Carrinho não encontrado para o usuário com ID $userId.")
        carrinhoRepository.delete(cart)
        log.info("Carrinho do usuário com ID $userId deletado com sucesso.")
    }

    companion object {
        private val log = LoggerFactory.getLogger(CarrinhoService::class.java)
    }
}