package com.andreev.ocrbackend.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
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

    @Column(name = "email")
    var email: String,

    @Column(name = "password")
    @JsonIgnore
    var password: String,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    val projects: Set<UserProjectAgent>? = null,
)
