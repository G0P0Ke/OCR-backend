package com.andreev.ocrbackend.core.model

import com.andreev.ocrbackend.core.model.security.Role
import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.Table

@Entity
@Table(name = "user_project_agent")
data class UserProjectAgent(
    @EmbeddedId
    val id: UserProjectAgentId,

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "id_user", insertable = false, updatable = false)
    val user: User,

    @ManyToOne
    @MapsId("idRole")
    @JoinColumn(name = "id_role", insertable = false, updatable = false)
    val role: Role,

    @ManyToOne
    @MapsId("idProject")
    @JoinColumn(name = "id_project", insertable = false, updatable = false)
    val project: Project
)

@Embeddable
data class UserProjectAgentId(
    @Column(name = "id_user")
    val idUser: UUID,
    @Column(name = "id_role")
    val idRole: Int,
    @Column(name = "id_project")
    val idProject: UUID
) : Serializable