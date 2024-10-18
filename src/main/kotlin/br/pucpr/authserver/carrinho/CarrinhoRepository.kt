package br.pucpr.authserver.carrinho

import br.pucpr.authserver.products.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CarrinhoRepository : JpaRepository<Carrinho, Long> {
}
