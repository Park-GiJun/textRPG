package com.gijun.textrpg.configuration

import com.gijun.textrpg.common.util.logger
import com.gijun.textrpg.common.util.PerformanceLogger
import com.gijun.textrpg.common.util.debug
import com.gijun.textrpg.common.util.error
import com.gijun.textrpg.common.util.info
import kotlinx.coroutines.reactor.awaitSingle
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.time.measureTimedValue

@Aspect
@Component
class LoggingAspect {
    
    private val logger = logger()
    
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    fun restControllerMethods() {}
    
    @Pointcut("@within(org.springframework.stereotype.Service)")
    fun serviceMethods() {}
    
    @Around("restControllerMethods()")
    fun logRestEndpoints(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature
        val className = signature.declaringType.simpleName
        val methodName = signature.name
        val args = joinPoint.args.joinToString(", ") { it?.toString() ?: "null" }
        
        logger.info { "🌐 REST Call: $className.$methodName($args)" }
        
        val (result, duration) = measureTimedValue {
            try {
                joinPoint.proceed()
            } catch (e: Exception) {
                logger.error(e) { "❌ REST Error in $className.$methodName" }
                throw e
            }
        }
        
        // Log performance metrics
        PerformanceLogger.log(
            operation = "rest-endpoint",
            duration = duration.inWholeMilliseconds,
            metadata = mapOf(
                "class" to className,
                "method" to methodName
            )
        )
        
        when (result) {
            is Mono<*> -> {
                return result.doOnSuccess {
                    logger.info { "✅ REST Success: $className.$methodName - ${duration.inWholeMilliseconds}ms" }
                }.doOnError { error ->
                    logger.error(error) { "❌ REST Error: $className.$methodName" }
                }
            }
            is Flux<*> -> {
                return result.doOnComplete {
                    logger.info { "✅ REST Stream Complete: $className.$methodName" }
                }.doOnError { error ->
                    logger.error(error) { "❌ REST Stream Error: $className.$methodName" }
                }
            }
            else -> {
                logger.info { "✅ REST Success: $className.$methodName - ${duration.inWholeMilliseconds}ms" }
                return result
            }
        }
    }
    
    @Around("serviceMethods() && execution(* com.gijun.textrpg.application.service.*.*(..))")
    fun logServiceMethods(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature
        val methodName = signature.name
        
        // Skip logging for simple getter methods
        if (methodName.startsWith("get") || methodName.startsWith("is")) {
            return joinPoint.proceed()
        }
        
        logger.debug { "🔧 Service: $methodName" }
        
        return try {
            val result = joinPoint.proceed()
            logger.debug { "✅ Service complete: $methodName" }
            result
        } catch (e: Exception) {
            logger.error(e) { "❌ Service error: $methodName" }
            throw e
        }
    }
}
