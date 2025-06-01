package com.gijun.textrpg.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import kotlin.reflect.KClass
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

// Logger 확장 함수
inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

// 더 간단한 Logger 생성
fun <T : Any> loggerFor(clazz: KClass<T>): Logger = LoggerFactory.getLogger(clazz.java)

// Logger 확장 함수들
inline fun Logger.debug(exception: Throwable? = null, message: () -> String) {
    if (isDebugEnabled) {
        if (exception != null) {
            debug(message(), exception)
        } else {
            debug(message())
        }
    }
}

inline fun Logger.info(exception: Throwable? = null, message: () -> String) {
    if (isInfoEnabled) {
        if (exception != null) {
            info(message(), exception)
        } else {
            info(message())
        }
    }
}

inline fun Logger.warn(exception: Throwable? = null, message: () -> String) {
    if (isWarnEnabled) {
        if (exception != null) {
            warn(message(), exception)
        } else {
            warn(message())
        }
    }
}

inline fun Logger.error(exception: Throwable? = null, message: () -> String) {
    if (isErrorEnabled) {
        if (exception != null) {
            error(message(), exception)
        } else {
            error(message())
        }
    }
}

// 성능 측정 로깅
inline fun <T> Logger.logExecutionTime(
    operationName: String,
    logLevel: LogLevel = LogLevel.DEBUG,
    block: () -> T
): T {
    val (result, duration) = measureTimedValue {
        try {
            block()
        } catch (e: Exception) {
            error(e) { "Error during $operationName" }
            throw e
        }
    }
    
    val message = "$operationName completed in ${duration.inWholeMilliseconds}ms"
    when (logLevel) {
        LogLevel.DEBUG -> debug { message }
        LogLevel.INFO -> info { message }
        LogLevel.WARN -> warn { message }
        LogLevel.ERROR -> error { message }
    }
    
    return result
}

// 성능 로거 (별도 파일에 기록)
object PerformanceLogger {
    private val logger = LoggerFactory.getLogger("performance")
    
    fun log(operation: String, duration: Long, metadata: Map<String, Any> = emptyMap()) {
        val metadataStr = metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }
        logger.info("PERF|$operation|${duration}ms|$metadataStr")
    }
}

// 구조화된 로깅을 위한 MDC 헬퍼
inline fun <T> withLoggingContext(
    vararg pairs: Pair<String, String>,
    block: () -> T
): T {
    val previousValues = mutableMapOf<String, String?>()
    
    try {
        // 기존 값 저장 및 새 값 설정
        pairs.forEach { (key, value) ->
            previousValues[key] = MDC.get(key)
            MDC.put(key, value)
        }
        
        return block()
    } finally {
        // 원래 값으로 복원
        previousValues.forEach { (key, previousValue) ->
            if (previousValue != null) {
                MDC.put(key, previousValue)
            } else {
                MDC.remove(key)
            }
        }
    }
}

// 로그 레벨 enum
enum class LogLevel {
    DEBUG, INFO, WARN, ERROR
}

// 사용 예시를 위한 주석
/**
 * 사용 예시:
 * 
 * class MyService {
 *     private val logger = logger() // 자동으로 클래스 이름으로 Logger 생성
 *     
 *     fun doSomething() {
 *         logger.debug { "Starting operation" } // 람다로 전달하여 성능 최적화
 *         
 *         logger.logExecutionTime("database query") {
 *             // 시간이 측정될 코드
 *         }
 *         
 *         withLoggingContext("userId" to "123", "operation" to "update") {
 *             logger.info { "User operation performed" } // MDC에 userId와 operation이 포함됨
 *         }
 *     }
 * }
 */
