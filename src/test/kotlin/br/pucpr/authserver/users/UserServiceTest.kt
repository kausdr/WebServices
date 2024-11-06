package br.pucpr.authserver.users

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.security.Jwt
import br.pucpr.authserver.users.Stubs.userStub
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.checkUnnecessaryStub
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

internal class UserServiceTest {
    private val usersRepositoryMock = mockk<UserRepository>()
    private val rolesRepositoryMock = mockk<RoleRepository>()
    private val jwtMock = mockk<Jwt>()

    private val service = UserService(usersRepositoryMock, rolesRepositoryMock, jwtMock)

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub()
    }

    @Test
    fun `Delete should return false if the user does not exist`() {
        every { usersRepositoryMock.findByIdOrNull(1) } returns null
        service.delete(1) shouldBe false
    }

    @Test
    fun `Delete must return true if the user is deleted`() {
        val user = userStub()
        every { usersRepositoryMock.findByIdOrNull(1) } returns user
        justRun { usersRepositoryMock.delete(user) }
        service.delete(1) shouldBe true
    }

    @Test
    fun `Delete should throw as BadRequestException if the user is the last admin`() {
        every {
            usersRepositoryMock.findByIdOrNull(1)
        } returns userStub(roles = listOf("ADMIN"))
        every {
            usersRepositoryMock.findByRole("ADMIN")
        } returns listOf(userStub(roles = listOf("ADMIN")))
        shouldThrow<BadRequestException> {
            service.delete(1)
        } shouldHaveMessage "Não é possível excluir o último administrador!"
    }

    @Test
    fun `Insert throws BadRequestException if the email exists`() {
        val user = user(email="user@email.com")
        every { usersRepositoryMock.findByEmail(user.email) } returns user

        assertThrows<BadRequestException> {
            service.insert(user)
        }
    }

    @Test
    fun `Insert saves the data if the email does not exists`() {
        val user = user(id=null)
        val newUser = user(id=1)
        every { usersRepositoryMock.findByEmail(user.email) } returns null
        every { usersRepositoryMock.save(user) } returns newUser
        service.insert(user) shouldBe newUser
    }

    @Test
    fun `Insert throws IllegalArgumentException if the user has an id`() {
        val userWithId = user(id=1)
        assertThrows<IllegalArgumentException> {
            service.insert(userWithId)
        }
    }
}
