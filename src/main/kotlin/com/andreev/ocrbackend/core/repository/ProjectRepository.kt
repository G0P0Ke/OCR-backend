package com.andreev.ocrbackend.core.repository

import com.andreev.ocrbackend.core.model.Project
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProjectRepository : JpaRepository<Project, UUID>