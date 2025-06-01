package com.gijun.textrpg.adapter.`in`.web.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,
    
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Invalid email format")
    val email: String,
    
    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "Username cannot be blank")
    val username: String,
    
    @field:NotBlank(message = "Password cannot be blank")
    val password: String
)

data class AuthResponse(
    val id: String,
    val username: String,
    val email: String,
    val roles: List<String>,
    val token: String? = null,
    val expiresIn: Long? = null
)
