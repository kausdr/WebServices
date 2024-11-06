package br.pucpr.authserver.produto

import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.produto.requests.CreateProductRequest
import br.pucpr.authserver.users.SortDir
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class ProductService(
    val repository: ProductRepository
) {
    fun insert(product: Product): Product {
        log.info("Novo produto criado: Nome='${product.name}', Preço=${product.price}")
        return repository.save(product)
    }

    fun update(id: Long, request: CreateProductRequest, auth: Authentication): Product {
        val existingProduct = repository.findById(id)
            .orElseThrow { NotFoundException("Produto $id não encontrado!") }

        val updatedProduct = existingProduct.copy(
            name = request.name?.takeIf { it.isNotBlank() } ?: existingProduct.name,
            description = request.description?.takeIf { it.isNotBlank() } ?: existingProduct.description,
            price = request.price?.takeIf { it > 0 } ?: existingProduct.price
        )

        val userName = auth.name
        log.info("Usuário '$userName' está atualizando o produto com ID $id")

        if (existingProduct.name != updatedProduct.name) {
            log.info("Produto $id: Nome alterado de '${existingProduct.name}' para '${updatedProduct.name}'")
        }
        if (existingProduct.description != updatedProduct.description) {
            log.info("Produto $id: Descrição alterada de '${existingProduct.description}' para '${updatedProduct.description}'")
        }
        if (existingProduct.price != updatedProduct.price) {
            log.info("Produto $id: Preço alterado de ${existingProduct.price} para ${updatedProduct.price}")
        }

        return repository.save(updatedProduct)
    }

    fun delete(id: Long) {
        val product = repository.findById(id)
            .orElseThrow { NotFoundException("Produto $id não encontrado!") }
        log.info("Produto excluído: Nome='${product.name}', ID=$id")
        repository.delete(product)
    }

    fun list(name: String?, sortDir: SortDir): List<Product> {
        return if (name != null) {
            repository.findByNameContainingIgnoreCase(name)
        } else {
            when (sortDir) {
                SortDir.ASC -> repository.findAll(Sort.by("name").ascending())
                SortDir.DESC -> repository.findAll(Sort.by("name").descending())
            }
        }
    }

    fun findById(id: Long): Product = repository.findById(id)
        .orElseThrow { NotFoundException("Produto $id não encontrado!") }

    companion object {
        private val log = LoggerFactory.getLogger(ProductService::class.java)
    }
}
