package com.andreev.ocrbackend.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OrderBy
import javax.persistence.Table

@Entity
@Table(name = "project")
@EntityListeners(AuditingEntityListener::class)
data class Project(
    @Id
    @Column(name = "id")
    @JsonIgnore
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name")
    val name: String,

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @JsonIgnore
    val participants: Set<UserProjectAgent>? = null,

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)], orphanRemoval = true)
    @OrderBy("id_model")
    @JsonIgnore
    val models: Set<Model>? = null,
)
