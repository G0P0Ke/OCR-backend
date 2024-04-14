package com.andreev.ocrbackend.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "document")
@EntityListeners(AuditingEntityListener::class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
data class Document(
    @Id
    @Column(name = "id")
    @JsonIgnore
    val id: UUID = UUID.randomUUID(),

    @Column(name = "labels")
    @Type(type = "jsonb")
    var labels: JsonNode? = null,

    @Column(name = "is_learnt")
    var isLearnt: Boolean? = null,

    @Column(name = "is_valid")
    var isValid: Boolean? = null,

    @Column(name = "is_labeled")
    var isLabeled: Boolean = false,

    @Column(name = "type")
    val type: String? = null,

    @Column(name = "url_path", nullable = false)
    val urlPath: String?,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "id_project", nullable = false)
    var project: Project,

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)])
    @JsonIgnore
    val assessors: MutableSet<UserDocumentAgent>? = mutableSetOf(),
) {
    override fun toString(): String {
        return "Document(id: $id, isLabeled: $isLabeled, createdAt: $createdAt)"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Document
        return id == other.id
    }
}
