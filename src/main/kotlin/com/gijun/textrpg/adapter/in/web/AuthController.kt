package com.gijun.textrpg.adapter.`in`.web

import com.gijun.textrpg.adapter.`in`.web.dto.LoginRequest
import com.gijun.textrpg.adapter.`in`.web.dto.RegisterRequest
import com.gijun.textrpg.adapter.`in`.web.dto.AuthResponse
import com.gijun.textrpg.application.port.`in`.AuthUseCase
import com.gijun.textrpg.application.port.`in`.LoginCommand
import com.gijun.textrpg.application.port.`in`.RegisterCommand
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authUseCase: AuthUseCase
) {

    @PostMapping("/register")
    suspend fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<AuthResponse> {
        val command = RegisterCommand(
            username = request.username,
            email = request.email,
            password = request.password
        )
        
        val user = authUseCase.register(command)
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(AuthResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                roles = user.roles.map { it.name },
                token = null // 등록 시에는 토큰 없음
            ))
    }

    @PostMapping("/login")
    suspend fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<AuthResponse> {
        val command = LoginCommand(
            username = request.username,
            password = request.password
        )
        
        val authResult = authUseCase.login(command)
        
        return ResponseEntity.ok(AuthResponse(
            id = authResult.user.id,
            username = authResult.user.username,
            email = authResult.user.email,
            roles = authResult.user.roles.map { it.name },
            token = authResult.token,
            expiresIn = authResult.expiresIn
        ))
    }

    @GetMapping("/me")
    suspend fun getCurrentUser(
        @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<AuthResponse> {
        val token = authorization.removePrefix("Bearer ")
        val user = authUseCase.validateToken(token)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        
        return ResponseEntity.ok(AuthResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            roles = user.roles.map { it.name },
            token = null
        ))
    }
}
