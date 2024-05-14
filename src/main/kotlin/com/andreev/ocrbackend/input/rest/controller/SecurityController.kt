package com.andreev.ocrbackend.input.rest.controller

import com.andreev.ocrbackend.core.model.security.RoleName
import com.andreev.ocrbackend.core.service.UserService
import com.andreev.ocrbackend.core.service.domain.security.JwtResponse
import com.andreev.ocrbackend.dto.EntryDto
import com.andreev.ocrbackend.dto.UserResponse
import com.andreev.ocrbackend.input.rest.converter.UserConverter
import io.swagger.v3.oas.annotations.Operation
import mu.KLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/security")
class SecurityController(
    private val userService: UserService,
    private val userConverter: UserConverter,
) {

    companion object : KLogging()

    @PostMapping(value = ["/register"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Регистрация пользователей")
    fun register(@Valid @RequestBody request: EntryDto): ResponseEntity<UserResponse> {
        val user = userService.register(entryDto = request, roleName = RoleName.ROLE_USER)
        return ResponseEntity.ok(userConverter.toResponse(user))
    }

    @PostMapping(value = ["/login"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Вход в систему и получения JWT")
    fun login(
        @Valid @RequestBody entryDto: EntryDto,
        response: HttpServletResponse
    ): ResponseEntity<JwtResponse> {
        val result = userService.login(entryDto)
        return if (result.first) {
            addJWTtoCookie(jwtResponse = result.second, response = response)
            logger.info { "Add cookie to header for ${entryDto.email}" }
            ResponseEntity.ok(result.second)
        } else ResponseEntity.badRequest().body(result.second)
    }

    @PostMapping(value = ["/private/register"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Добавление администраторов в систему")
    fun privateRegister(@Valid @RequestBody request: EntryDto): ResponseEntity<UserResponse> {
        val user = userService.register(entryDto = request, roleName = RoleName.ROLE_ADMIN)
        return ResponseEntity.ok(userConverter.toResponse(user))
    }

    fun addJWTtoCookie(jwtResponse: JwtResponse, response: HttpServletResponse) {
        val cookie = Cookie("accessToken", jwtResponse.accessToken)
        cookie.isHttpOnly = false
        cookie.secure = false
        cookie.path = "/" // Makes the cookie available for all paths
        cookie.maxAge = 3600 // Sets the cookie to expire after 1 hour
        response.addCookie(cookie)
    }
}