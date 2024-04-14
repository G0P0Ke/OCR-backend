package com.andreev.ocrbackend.input.rest.controller

import com.andreev.ocrbackend.core.service.UserService
import com.andreev.ocrbackend.dto.UserResponse
import com.andreev.ocrbackend.input.rest.converter.UserConverter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/user")
class UserController(
    private val userService: UserService,
    private val userConverter: UserConverter,
) {

    @GetMapping("")
    fun getByCompany(
        @RequestParam(name = "company", required = true) company: String
    ) : ResponseEntity<List<UserResponse>> {
        val userList = userService.findUsersByCompany(company)
        return ResponseEntity.ok(userConverter.collectionToResponse(userList))
    }
}