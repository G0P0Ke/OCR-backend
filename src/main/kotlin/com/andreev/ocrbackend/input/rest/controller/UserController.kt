package com.andreev.ocrbackend.input.rest.controller

import com.andreev.ocrbackend.core.service.UserService
import com.andreev.ocrbackend.dto.UserResponse
import com.andreev.ocrbackend.input.rest.converter.UserConverter
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/v1/user")
class UserController(
    private val userService: UserService,
    private val userConverter: UserConverter,
) {

    @GetMapping("")
    @Operation(
        summary = "Получение пользователей по фильтру",
        description = "Получение пользователей по конкретной компании"
    )
    fun getByCompany(
        @RequestParam(name = "company", required = true) company: String
    ): ResponseEntity<List<UserResponse>> {
        val userList = userService.findUsersByCompany(company)
        return ResponseEntity.ok(userConverter.collectionToResponse(userList))
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение пользователя по идентификатору",
        description = "Вся информация о пользователе"
    )
    fun getInfo(@PathVariable id: UUID) : ResponseEntity<UserResponse> {
        val user = userService.findById(id)
        return ResponseEntity.ok(userConverter.toResponse(user))
    }
}