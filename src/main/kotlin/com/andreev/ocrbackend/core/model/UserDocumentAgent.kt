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
@Table(name = "user_document_agent")
data class UserDocumentAgent(
    @EmbeddedId
    val id: UserDocumentAgentId,

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "id_user", insertable = false, updatable = false)
    val user: User,

    @ManyToOne
    @MapsId("idDocument")
    @JoinColumn(name = "id_document", insertable = false, updatable = false)
    val document: Document
)

@Embeddable
data class UserDocumentAgentId(
    @Column(name = "id_user")
    val idUser: UUID,
    @Column(name = "id_document")
    val idDocument: UUID
) : Serializable