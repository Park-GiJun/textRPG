package com.gijun.textrpg.configuration

import com.gijun.textrpg.application.port.out.UserRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return mono {
            val token = authentication.credentials.toString()
            
            if (!jwtUtil.validateToken(token)) {
                throw RuntimeException("Invalid JWT token")
            }
            
            val username = jwtUtil.getUsernameFromToken(token)
            val user = userRepository.findByUsername(username)
                ?: throw RuntimeException("User not found")
            
            if (!user.isActive) {
                throw RuntimeException("User account is deactivated")
            }
            
            val authorities = user.roles.map { SimpleGrantedAuthority("ROLE_${it.name}") }
            
            UsernamePasswordAuthenticationToken(user.username, null, authorities)
        }
    }
}
