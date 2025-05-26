package com.gijun.textrpg.common.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Reactive Extensions
suspend fun <T> Mono<T>.awaitSingle(): T = this.awaitFirst()
suspend fun <T> Mono<T>.awaitSingleOrNull(): T? = this.awaitFirstOrNull()
fun <T> Flux<T>.asFlow(): Flow<T> = this.asFlow()

// Flow Extensions
suspend fun <T> Flow<T>.toList(): List<T> {
    val result = mutableListOf<T>()
    collect { result.add(it) }
    return result
}

fun <T, R> Flow<T>.mapNotNull(transform: suspend (T) -> R?): Flow<R> = 
    this.map { transform(it) }.map { it!! }

// Page Extensions
suspend fun <T> Flow<T>.toPage(pageable: Pageable, totalSupplier: suspend () -> Long): Page<T> {
    val content = this.toList()
    val total = totalSupplier()
    return PageImpl(content, pageable, total)
}

// Date/Time Extensions
fun LocalDateTime.toIsoString(): String = 
    this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

fun String.toLocalDateTime(): LocalDateTime = 
    LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

// String Extensions
fun String.toSlug(): String = 
    this.lowercase()
        .replace(Regex("[^a-z0-9\\s-]"), "")
        .replace(Regex("\\s+"), "-")
        .trim('-')

fun String?.isNotNullOrBlank(): Boolean = 
    !this.isNullOrBlank()

// Collection Extensions
fun <T> List<T>.second(): T = this[1]
fun <T> List<T>.third(): T = this[2]

inline fun <T> List<T>.firstOrElse(default: () -> T): T = 
    firstOrNull() ?: default()

// Result Extensions
inline fun <T> runCatchingAsync(crossinline block: suspend () -> T): Result<T> =
    try {
        Result.success(kotlinx.coroutines.runBlocking { block() })
    } catch (e: Exception) {
        Result.failure(e)
    }

// Validation Extensions
fun String.isValidEmail(): Boolean = 
    Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")
        .matches(this)

fun String.isValidUUID(): Boolean = 
    try {
        java.util.UUID.fromString(this)
        true
    } catch (e: IllegalArgumentException) {
        false
    }

// Pagination Helper
data class PageRequest(
    val page: Int = 0,
    val size: Int = 20,
    val sort: String? = null
) {
    init {
        require(page >= 0) { "Page must be non-negative" }
        require(size > 0) { "Size must be positive" }
        require(size <= 100) { "Size cannot exceed 100" }
    }
    
    fun toPageable(): Pageable = 
        org.springframework.data.domain.PageRequest.of(page, size)
}

// Response Wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> =
            ApiResponse(success = true, data = data, message = message)
            
        fun <T> error(message: String): ApiResponse<T> =
            ApiResponse(success = false, data = null, message = message)
    }
}
