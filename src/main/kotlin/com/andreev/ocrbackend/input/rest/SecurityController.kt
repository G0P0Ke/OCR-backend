package com.andreev.ocrbackend.input.rest

import com.andreev.ocrbackend.core.service.UserService
import com.andreev.ocrbackend.core.service.domain.security.JwtResponse
import com.andreev.ocrbackend.core.service.security.jwt.JwtProvider
import com.andreev.ocrbackend.dto.EntryDto
import com.andreev.ocrbackend.dto.UserResponse
import com.andreev.ocrbackend.input.rest.converter.UserConverter
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/security")
class SecurityController(
    private val userService: UserService,
    private val userConverter: UserConverter,
    private val authenticationManager: AuthenticationManager,
    private val jwtProvider: JwtProvider
) {

    @PostMapping(value = ["/register"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Регистрация пользователей")
    fun register(@Valid @RequestBody entryDto: EntryDto) : ResponseEntity<UserResponse> {
        val user = userService.register(email = entryDto.email, password = entryDto.password)
        return ResponseEntity.ok(userConverter.toResponse(user))
    }

    @PostMapping(value = ["/login"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Вход в систему и получения JWT")
    fun login(@Valid @RequestBody entryDto: EntryDto) : ResponseEntity<JwtResponse> {
        val exists = userService.existsUser(entryDto.email)
        if (exists) {
            val authToken = UsernamePasswordAuthenticationToken(entryDto.email, entryDto.password)
            val authentication: Authentication = authenticationManager.authenticate(authToken)
            SecurityContextHolder.getContext().authentication = authentication

            val jwtToken: String = jwtProvider.generateJwtToken(authentication)

            return ResponseEntity.ok(JwtResponse(jwtToken))
        } else {
            return ResponseEntity.badRequest().body(JwtResponse(""))
        }
    }
}