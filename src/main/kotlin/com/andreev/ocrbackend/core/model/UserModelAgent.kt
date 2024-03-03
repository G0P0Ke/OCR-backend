package com.andreev.ocrbackend.core.model

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
@Table(name = "user_model_agent")
data class UserModelAgent(
    @EmbeddedId
    val id: UserModelAgentId,

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "id_user", insertable = false, updatable = false)
    val user: User,

    @ManyToOne
    @MapsId("idModel")
    @JoinColumn(name = "id_model", insertable = false, updatable = false)
    val model: Model
)

@Embeddable
data class UserModelAgentId(
    @Column(name = "id_user")
    val idUser: UUID,
    @Column(name = "id_model")
    val idModel: UUID
) : Serializable