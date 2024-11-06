package br.pucpr.authserver.produto

import br.pucpr.authserver.produto.requests.CreateProductRequest
import br.pucpr.authserver.produto.responses.ProductResponse
import br.pucpr.authserver.users.SortDir
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    val service: ProductService
) {

    @Transactional
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
    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("permitAll()")
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
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name="AuthServer")
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
