package com.gijun.textrpg.application.service

import com.gijun.textrpg.application.port.`in`.AuthUseCase
import com.gijun.textrpg.application.port.`in`.AuthResult
import com.gijun.textrpg.application.port.`in`.LoginCommand
import com.gijun.textrpg.application.port.`in`.RegisterCommand
import com.gijun.textrpg.application.port.out.UserRepository
import com.gijun.textrpg.configuration.JwtUtil
import com.gijun.textrpg.domain.user.User
import com.gijun.textrpg.domain.user.UserRole
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) : AuthUseCase {

    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    override suspend fun register(command: RegisterCommand): User {
        logger.info("Registering new user: ${command.username}")

        validateUserDoesNotExist(command)
        
        val user = createUser(command)
        val savedUser = userRepository.save(user)

        logger.info("User registered successfully: ${savedUser.id}")
        return savedUser
    }

    override suspend fun login(command: LoginCommand): AuthResult {
        logger.info("User login attempt: ${command.username}")

        val user = userRepository.findByUsername(command.username)
            ?: throw AuthenticationException("Invalid username or password")

        if (!user.isActive) {
            throw AuthenticationException("Account is deactivated")
        }

        if (!passwordEncoder.matches(command.password, user.passwordHash)) {
            throw AuthenticationException("Invalid username or password")
        }

        val authorities = user.roles.map { "ROLE_${it.name}" }
        val token = jwtUtil.generateToken(user.username, authorities)

        logger.info("User logged in successfully: ${user.id}")
        return AuthResult(
            user = user,
            token = token,
            expiresIn = 86400000L // 24시간
        )
    }

    override suspend fun validateToken(token: String): User? {
        return try {
            if (!jwtUtil.validateToken(token)) {
                return null
            }
            
            val username = jwtUtil.getUsernameFromToken(token)
            userRepository.findByUsername(username)?.takeIf { it.isActive }
        } catch (e: Exception) {
            logger.debug("Token validation failed", e)
            null
        }
    }

    private suspend fun validateUserDoesNotExist(command: RegisterCommand) {
        if (userRepository.existsByUsername(command.username)) {
            throw UserAlreadyExistsException("Username '${command.username}' already exists")
        }
        
        if (userRepository.existsByEmail(command.email)) {
            throw UserAlreadyExistsException("Email '${command.email}' already exists")
        }
    }

    private fun createUser(command: RegisterCommand): User {
        val passwordHash = passwordEncoder.encode(command.password)
        
        return User(
            username = command.username,
            email = command.email,
            passwordHash = passwordHash,
            roles = setOf(UserRole.USER)
        )
    }
}

// 인증 관련 예외
class AuthenticationException(message: String) : RuntimeException(message)
class UserAlreadyExistsException(message: String) : RuntimeException(message)
class UserNotFoundException(message: String) : RuntimeException(message)
