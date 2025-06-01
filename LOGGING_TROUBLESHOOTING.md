# TextRPG 로깅 트러블슈팅 가이드

## 일반적인 문제 해결

### 1. Logback 초기화 오류

#### 증상
```
Logging system failed to initialize using configuration from 'null'
java.lang.IllegalStateException: Logback configuration error detected
```

#### 해결방법

1. **기본 Spring Boot 로깅 사용**
   - `logback-spring.xml` 파일을 삭제하거나 이름 변경
   - Spring Boot의 기본 로깅 설정 사용

2. **간단한 Logback 설정 사용**
   ```bash
   # logback-spring.xml을 logback-spring-complex.xml로 변경
   mv src/main/resources/logback-spring.xml src/main/resources/logback-spring-complex.xml
   
   # logback-spring-simple.xml을 logback-spring.xml로 변경
   mv src/main/resources/logback-spring-simple.xml src/main/resources/logback-spring.xml
   ```

3. **application.yml 설정만 사용**
   ```yaml
   logging:
     level:
       root: INFO
       com.gijun.textrpg: DEBUG
     file:
       name: logs/textrpg.log
     pattern:
       console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
       file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
   ```

### 2. 로그 파일이 생성되지 않는 경우

#### 원인
- 로그 디렉토리 권한 문제
- 경로 설정 오류

#### 해결방법

1. **로그 디렉토리 생성**
   ```bash
   mkdir logs
   ```

2. **권한 확인 (Linux/Mac)**
   ```bash
   chmod 755 logs
   ```

3. **절대 경로 사용**
   ```yaml
   logging:
     file:
       name: C:/projects/textrpg/logs/textrpg.log  # Windows
       # name: /var/log/textrpg/textrpg.log        # Linux
   ```

### 3. 로그 레벨이 적용되지 않는 경우

#### 해결방법

1. **Spring Profile 확인**
   ```bash
   # 실행 시 프로필 지정
   java -jar textrpg.jar --spring.profiles.active=local
   ```

2. **환경 변수로 설정**
   ```bash
   export SPRING_PROFILES_ACTIVE=local
   ```

3. **application.yml에서 직접 설정**
   ```yaml
   spring:
     profiles:
       active: local
   ```

### 4. 콘솔에 색상이 표시되지 않는 경우

#### Windows 환경
```yaml
spring:
  output:
    ansi:
      enabled: always
```

#### IDE 설정
- IntelliJ IDEA: Run Configuration → "Enable ANSI colors in output console" 체크
- VS Code: 기본적으로 지원

### 5. 로그 파일 크기가 너무 큰 경우

#### 즉시 해결
```bash
# 현재 로그 파일 비우기
echo "" > logs/textrpg.log

# 오래된 로그 삭제
find logs -name "*.log.gz" -mtime +7 -delete  # 7일 이상된 로그 삭제
```

#### 영구적 해결
```yaml
logging:
  logback:
    rollingpolicy:
      max-file-size: 10MB     # 파일 크기 줄이기
      max-history: 7          # 보관 기간 줄이기
```

## 디버깅 팁

### 1. Logback 상태 확인
```java
// Application 시작 시 추가
import ch.qos.logback.core.util.StatusPrinter;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

StatusPrinter.print((LoggerContext) LoggerFactory.getILoggerFactory());
```

### 2. 로그 레벨 동적 변경
```kotlin
// 런타임에 로그 레벨 변경
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("com.gijun.textrpg") as Logger
logger.level = Level.DEBUG
```

### 3. 특정 패키지만 디버그 모드
```yaml
logging:
  level:
    com.gijun.textrpg.adapter.out.persistence: TRACE  # SQL 쿼리 확인
    org.springframework.r2dbc: DEBUG                   # R2DBC 디버깅
```

## 성능 최적화

### 1. 비동기 로깅 사용
```xml
<appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <queueSize>512</queueSize>
    <discardingThreshold>0</discardingThreshold>
    <appender-ref ref="FILE"/>
</appender>
```

### 2. 조건부 로깅
```kotlin
// 비효율적
logger.debug("Heavy computation: " + heavyComputation())

// 효율적
logger.debug { "Heavy computation: ${heavyComputation()}" }

// 또는
if (logger.isDebugEnabled) {
    logger.debug("Heavy computation: ${heavyComputation()}")
}
```

## 프로덕션 체크리스트

- [ ] 로그 레벨을 INFO 이상으로 설정
- [ ] 콘솔 출력 비활성화
- [ ] 로그 파일 롤링 정책 확인
- [ ] 디스크 공간 모니터링 설정
- [ ] 민감한 정보 로깅 제거
- [ ] 성능에 영향을 주는 DEBUG 로그 제거

## 추가 리소스

- [Spring Boot Logging 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
- [Logback 공식 문서](http://logback.qos.ch/documentation.html)
- [Kotlin Logging](https://github.com/MicroUtils/kotlin-logging)
