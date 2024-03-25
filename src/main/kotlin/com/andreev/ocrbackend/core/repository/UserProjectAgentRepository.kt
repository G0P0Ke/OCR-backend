package com.andreev.ocrbackend.core.repository

import com.andreev.ocrbackend.core.model.UserProjectAgent
import com.andreev.ocrbackend.core.model.UserProjectAgentId
import org.springframework.data.jpa.repository.JpaRepository

interface UserProjectAgentRepository : JpaRepository<UserProjectAgent, UserProjectAgentId>