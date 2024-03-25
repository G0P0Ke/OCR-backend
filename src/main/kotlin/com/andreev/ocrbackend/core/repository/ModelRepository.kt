package com.andreev.ocrbackend.core.repository

import com.andreev.ocrbackend.core.model.Model
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ModelRepository : JpaRepository<Model, UUID>