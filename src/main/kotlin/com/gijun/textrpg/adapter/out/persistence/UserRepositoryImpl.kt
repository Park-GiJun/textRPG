package com.gijun.textrpg.adapter.out.persistence

import com.gijun.textrpg.application.port.out.UserRepository
import com.gijun.textrpg.domain.user.User
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val r2dbcRepository: UserR2dbcRepository,
    private val userMapper: UserMapper
) : UserRepository {

    override suspend fun save(user: User): User {
        val entity = userMapper.toEntity(user)
        val savedEntity = r2dbcRepository.save(entity)
        return userMapper.toDomain(savedEntity)
    }

    override suspend fun findById(id: String): User? {
        return r2dbcRepository.findById(id)?.let { userMapper.toDomain(it) }
    }

    override suspend fun findByUsername(username: String): User? {
        return r2dbcRepository.findByUsername(username)?.let { userMapper.toDomain(it) }
    }

    override suspend fun findByEmail(email: String): User? {
        return r2dbcRepository.findByEmail(email)?.let { userMapper.toDomain(it) }
    }

    override suspend fun existsByUsername(username: String): Boolean {
        return r2dbcRepository.existsByUsername(username)
    }

    override suspend fun existsByEmail(email: String): Boolean {
        return r2dbcRepository.existsByEmail(email)
    }

    override suspend fun deleteById(id: String) {
        r2dbcRepository.deleteById(id)
    }

    override suspend fun count(): Long {
        return r2dbcRepository.count()
    }
}
