package com.andreev.ocrbackend.core.model

import com.andreev.ocrbackend.core.model.converter.JsonNodeToStringConverter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.jpa.domain.support.AuditingEntityListener
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
@Table(name = "model")
@EntityListeners(AuditingEntityListener::class)
data class Model(
    @Id
    @Column(name = "id")
    @JsonIgnore
    val id: UUID = UUID.randomUUID(),

    @Column(name = "labels")
    @Convert(converter = JsonNodeToStringConverter::class)
    val labels: JsonNode,

    @Column(name = "isLearnt")
    val isLearnt: Boolean?,

    @Column(name = "isValid")
    val isValid: Boolean?,

    @Column(name = "type")
    val type: String?,

    @ManyToOne
    @JoinColumn(name = "id_project", nullable = false)
    var project: Project? = null,

    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    @JsonIgnore
    val assessors: Set<UserModelAgent>? = null,
)
