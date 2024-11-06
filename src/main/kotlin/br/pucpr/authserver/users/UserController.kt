package br.pucpr.authserver.users

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.errors.ForbiddenException
import br.pucpr.authserver.security.UserToken
import br.pucpr.authserver.users.requests.CreateUserRequest
import br.pucpr.authserver.users.requests.LoginRequest
import br.pucpr.authserver.users.requests.UpdateUserRequest
import br.pucpr.authserver.users.responses.UserResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    val service: UserService,
) {

    @Transactional
    @PostMapping
    fun insert(@RequestBody @Valid user: CreateUserRequest) =
        UserResponse(service.insert(user.toUser()))
            .let { ResponseEntity.status(CREATED).body(it) }

    @GetMapping
    fun list(
        @RequestParam(required = false) sortDir: String?,
        @RequestParam(required = false) role: String?
    ) =
        service.list(
            sortDir = SortDir.getByName(sortDir) ?:
                throw BadRequestException("Invalid sort dir!"),
            role=role
        )
        .map { UserResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: Long) =
        service.findByIdOrNull(id)
            ?.let { UserResponse(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name="AuthServer")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Void> =
        service.delete(id)
            ?.let { ResponseEntity.ok().build() }
            ?: ResponseEntity.notFound().build()

    @PatchMapping("/{id}")
    @PreAuthorize("permitAll()")
    @SecurityRequirement(name="AuthServer")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody @Valid updateUserRequest: UpdateUserRequest,
        auth: Authentication
    ): ResponseEntity<UserResponse> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (token.id != id && !token.isAdmin) throw ForbiddenException()

        return service.update(id, updateUserRequest.name!!)
            ?.let { UserResponse(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}/roles/{role}")
    fun grant(
        @PathVariable("id") id: Long,
        @PathVariable role: String
    ): ResponseEntity<Void> =
        if (service.addRole(id, role.uppercase()))
            ResponseEntity.ok().build()
        else
            ResponseEntity.status(NO_CONTENT).build()

    @PostMapping("/login")
    fun login(@Valid @RequestBody credentials: LoginRequest) =
        service.login(credentials)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
}