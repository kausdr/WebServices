package br.pucpr.authserver.produto

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.produto.requests.CreateProductRequest
import br.pucpr.authserver.produto.responses.ProductResponse
import br.pucpr.authserver.users.SortDir
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    val service: ProductService
) {

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun insert(@RequestBody @Valid productRequest: CreateProductRequest): ResponseEntity<ProductResponse> {
        val product = service.insert(productRequest.toProduct())
        return ResponseEntity.status(CREATED).body(ProductResponse(product))
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid productRequest: CreateProductRequest,
        auth: Authentication
    ): ResponseEntity<ProductResponse> {
        val updatedProduct = service.update(id, productRequest, auth)
        return ResponseEntity.ok(ProductResponse(updatedProduct))
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
    ) =
        service.list(
            name = name,
            sortDir = SortDir.getByName(sortDir) ?:
                throw BadRequestException("Invalid sort dir!")
        )
        .map { ProductResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = service.findById(id)
        return ResponseEntity.ok(ProductResponse(product))
    }
}
