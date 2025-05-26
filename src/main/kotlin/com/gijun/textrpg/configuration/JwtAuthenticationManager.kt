package com.gijun.textrpg.configuration

import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
    private val jwtUtil: JwtUtil
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return mono {
            val token = authentication.credentials.toString()
            
            if (!jwtUtil.validateToken(token)) {
                throw RuntimeException("Invalid JWT token")
            }
            
            val claims = jwtUtil.getClaims(token)
            val username = claims.subject
            val authorities = (claims["authorities"] as? List<*>)
                ?.mapNotNull { it as? String }
                ?.map { SimpleGrantedAuthority(it) }
                ?: emptyList()
            
            UsernamePasswordAuthenticationToken(username, null, authorities)
        }
    }
}