package com.andreev.ocrbackend.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "model")
@EntityListeners(AuditingEntityListener::class)
data class Model(
    @Id
    @Column(name = "id")
    @JsonIgnore
    val id: UUID = UUID.randomUUID(),

    @Column(name = "type")
    val type: String? = null,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    val status: Status = Status.NEW,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToOne
    @JoinColumn(name = "id_project", referencedColumnName = "id", nullable = false)
    val project: Project
) {
    override fun toString(): String {
        return "Model(id: $id, type: $type, status: $status, createdAt: $createdAt)"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Model
        return id == other.id
    }

    enum class Status {
        NEW, IN_PROGRESS, FAILED, TAUGHT
    }
}
