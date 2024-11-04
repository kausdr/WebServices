package br.pucpr.authserver.users

import br.pucpr.authserver.roles.Role
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "tblUser")
class User (
    @Id @GeneratedValue
    var id: Long? = null,

    @NotNull
    var name: String,

    @Email
    @Column(unique = true, nullable = false)
    var email: String,

    @Column(length = 32, nullable = false)
    var password: String,

    @ManyToMany
    @JoinTable(
        name = "UsersRole",
        joinColumns = [JoinColumn(name="idUser")],
        inverseJoinColumns = [JoinColumn(name="idRole")]
    )
    val roles: MutableSet<Role> = mutableSetOf()
)