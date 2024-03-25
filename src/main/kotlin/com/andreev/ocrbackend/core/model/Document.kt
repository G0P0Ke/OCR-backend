package com.andreev.ocrbackend.core.model

import com.andreev.ocrbackend.core.model.converter.JsonNodeToStringConverter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Convert
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
data class Document(
    @Id
    @Column(name = "id")
    @JsonIgnore
    val id: UUID = UUID.randomUUID(),

    @Column(name = "labels")
    @Convert(converter = JsonNodeToStringConverter::class)
    val labels: JsonNode,

    @Column(name = "is_learnt")
    val isLearnt: Boolean?,

    @Column(name = "is_valid")
    val isValid: Boolean?,

    @Column(name = "type")
    val type: String?,

    @Column(name = "url_path", nullable = false)
    val urlPath: String?,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "id_project", nullable = false)
    var project: Project,

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    @JsonIgnore
    val assessors: MutableSet<UserDocumentAgent> = mutableSetOf(),
) {
    override fun toString(): String {
        return "Document(id: $id, labels: $labels, createdAt: $createdAt)"
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
