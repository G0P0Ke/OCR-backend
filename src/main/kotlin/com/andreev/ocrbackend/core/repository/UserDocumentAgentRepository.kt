package com.andreev.ocrbackend.core.repository

import com.andreev.ocrbackend.core.model.UserDocumentAgent
import com.andreev.ocrbackend.core.model.UserDocumentAgentId
import org.springframework.data.jpa.repository.JpaRepository

interface UserDocumentAgentRepository : JpaRepository<UserDocumentAgent, UserDocumentAgentId>