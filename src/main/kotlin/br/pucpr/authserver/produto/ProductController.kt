package br.pucpr.authserver.products

import br.pucpr.authserver.products.requests.CreateProductRequest
import br.pucpr.authserver.products.responses.ProductResponse
import br.pucpr.authserver.users.SortDir
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    val service: ProductService
) {

    @PostMapping
    fun insert(@RequestBody @Valid productRequest: CreateProductRequest): ResponseEntity<ProductResponse> {
        val product = service.insert(
            Product(
                name = productRequest.name,
                description = productRequest.description,
                price = productRequest.price
            )
        )
        return ResponseEntity.status(CREATED).body(ProductResponse(product))
    }

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid productRequest: CreateProductRequest
    ): ResponseEntity<ProductResponse> {
        val product = service.update(
            id, Product(
                name = productRequest.name,
                description = productRequest.description,
                price = productRequest.price
            )
        )
        return ResponseEntity.ok(ProductResponse(product))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) sortDir: String?
    ): ResponseEntity<List<ProductResponse>> {
        val products = service.list(
            name = name,
            sortDir = SortDir.getByName(sortDir) ?: SortDir.ASC
        ).map { ProductResponse(it) }
        return ResponseEntity.ok(products)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = service.findById(id)
        return ResponseEntity.ok(ProductResponse(product))
    }
}
