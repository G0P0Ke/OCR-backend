package com.andreev.ocrbackend.core.model

import com.andreev.ocrbackend.core.model.security.Role
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "surname")
    var surname: String? = null,

    @Column(name = "email", unique = true)
    var email: String,

    @Column(name = "company")
    var company: String? = null,

    @Column(name = "password")
    @JsonIgnore
    var password: String,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)])
    @JsonIgnore
    val projects: MutableSet<UserProjectAgent> = mutableSetOf(),

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)])
    @JsonIgnore
    val documents: MutableSet<UserDocumentAgent> = mutableSetOf(),

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = true)
    val role: Role? = null
) {
    override fun toString(): String {
        return "User(id: $id, name: $name, surname: $surname, email: $email, company: $company)"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        return id == other.id
    }
}
