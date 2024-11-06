package br.pucpr.authserver

import br.pucpr.authserver.produto.Product
import br.pucpr.authserver.produto.ProductRepository
import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.users.AdminConfig
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.PropertySource
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
@PropertySource("classpath:security.properties")
class Bootstrapper(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val adminConfig: AdminConfig,
    private val productRepository: ProductRepository
): ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole = roleRepository.findByName("ADMIN")
            ?: roleRepository.save(Role(
                name="ADMIN",
                description = "System Administrator")
            ).also {
                roleRepository.save(Role(
                    name="USER",
                    description = "Premium User")
                )
                log.info("ADMIN and USER roles created!")
            }
        if (userRepository.findByRole("ADMIN").isEmpty()) {
            val admin = User(
                email=adminConfig.email,
                password=adminConfig.password,
                name=adminConfig.name
            )
            admin.roles.add(adminRole)
            userRepository.save(admin)
            log.info("ADMIN user created!")
        }

        if (productRepository.count() == 0L) {
            val products = listOf(
                Product(
                    name = "Gaming Mouse",
                    description = "High precision gaming mouse with customizable RGB lighting",
                    price = 59.99
                ),
                Product(
                    name = "Mechanical Keyboard",
                    description = "Mechanical keyboard with Cherry MX switches and RGB lighting",
                    price = 129.99
                ),
                Product(
                    name = "4K Gaming Monitor",
                    description = "27-inch 4K UHD monitor with 144Hz refresh rate",
                    price = 399.99
                ),
                Product(
                    name = "Wireless Gaming Headset",
                    description = "Surround sound wireless headset with noise-canceling microphone",
                    price = 99.99
                ),
                Product(
                    name = "Gaming Chair",
                    description = "Ergonomic gaming chair with lumbar support and adjustable height",
                    price = 199.99
                )
            )

            productRepository.saveAll(products)
            log.info("Default products created!")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Bootstrapper::class.java)
    }
}