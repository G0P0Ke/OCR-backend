package com.andreev.ocrbackend.input.rest.controller

import com.andreev.ocrbackend.core.service.UserService
import com.andreev.ocrbackend.dto.UserResponse
import com.andreev.ocrbackend.dto.UserUpdateRequest
import com.andreev.ocrbackend.input.rest.converter.UserConverter
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

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
        @RequestParam(name = "company", required = true) company: String,
        authentication: Authentication
    ): ResponseEntity<List<UserResponse>> {
        val userList = userService.findUsersByCompany(company, authentication)
        return ResponseEntity.ok(userConverter.collectionToResponse(userList))
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получение всех пользователей системы")
    fun getAllUsers(authentication: Authentication): ResponseEntity<List<UserResponse>> {
        val userList = userService.getAllUsers(authentication)
        return ResponseEntity.ok(userConverter.collectionToResponse(collection = userList))
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Удаление пользователя")
    fun delete(@PathVariable id: UUID): ResponseEntity<String> {
        userService.deleteUser(userId = id)
        return ResponseEntity.ok("Successfully delete user with id: $id")
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Изменение данных пользователя по идентификатору")
    fun updateUser(
        @PathVariable id: UUID,
        @RequestBody @Valid request: UserUpdateRequest
    ): ResponseEntity<UserResponse> {
        val result = userService.updateUser(userId = id, request = request)
        return ResponseEntity.ok(userConverter.toResponse(user = result))
    }
}