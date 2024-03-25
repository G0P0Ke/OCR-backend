package com.andreev.ocrbackend.core.model.security

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name = "Role")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    val name: RoleName
)

enum class RoleName {
    ROLE_ADMIN, ROLE_MODERATOR, ROLE_MANAGER, ROLE_ASSESSOR, ROLE_USER
}