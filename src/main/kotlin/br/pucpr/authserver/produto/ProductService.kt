package br.pucpr.authserver.products

import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.users.SortDir
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ProductService(
    val repository: ProductRepository
) {
    fun insert(product: Product): Product = repository.save(product)

    fun update(id: Long, product: Product): Product {
        val existingProduct = repository.findById(id)
            .orElseThrow { NotFoundException("Produto $id não encontrado!") }

        val updatedProduct = existingProduct.copy(
            name = product.name,
            description = product.description,
            price = product.price
        )
        return repository.save(updatedProduct)
    }

    fun delete(id: Long) {
        val product = repository.findById(id)
            .orElseThrow { NotFoundException("Produto $id não encontrado!") }
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
}
