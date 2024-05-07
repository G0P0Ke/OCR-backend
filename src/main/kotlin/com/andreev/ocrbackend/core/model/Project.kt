package com.andreev.ocrbackend.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
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
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "project")
@EntityListeners(AuditingEntityListener::class)
data class Project(
    @Id
    @Column(name = "id")
    @JsonIgnore
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", unique = true)
    var name: String,

    @Column(name = "description")
    var description: String,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)], orphanRemoval = true)
    @JsonIgnore
    val participants: MutableSet<UserProjectAgent>? = null,

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)], orphanRemoval = true)
    @JsonIgnore
    val documents: MutableSet<Document>? = mutableSetOf(),

    @OneToOne(mappedBy = "project", fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)], orphanRemoval = true)
    @JsonIgnore
    val model: Model? = null
) {
    override fun toString(): String {
        return "Project(id: $id, name: $name, createdAt: $createdAt)"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Project
        return id == other.id
    }
}
