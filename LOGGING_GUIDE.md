# TextRPG 로깅 설정 가이드

TextRPG 프로젝트는 Spring Boot의 로깅 시스템을 사용합니다.
간단한 설정부터 고급 설정까지 단계별로 제공합니다.

## 빠른 시작 🚀

### 1단계: 기본 설정 사용 (권장)
아무 설정 없이 애플리케이션을 실행하면 Spring Boot의 기본 로깅이 작동합니다.

```bash
# 로그 디렉토리 생성
mkdir logs

# 애플리케이션 실행
./gradlew bootRun
```

### 2단계: application.yml로 커스터마이즈
`src/main/resources/application.yml`에서 로깅 레벨과 파일 위치를 설정:

```yaml
logging:
  level:
    root: INFO                    # 전체 로그 레벨
    com.gijun.textrpg: DEBUG     # 프로젝트 로그 레벨
  file:
    path: logs                   # 로그 파일 경로
```

### 3단계: 고급 설정 사용 (선택사항)
더 세밀한 제어가 필요한 경우:

1. `application.yml`에서 설정 파일 지정:
   ```yaml
   logging:
     config: classpath:logback-spring-advanced.xml
   ```

2. 애플리케이션 재시작

## 로깅 레벨 가이드

| 레벨 | 용도 | 예시 |
|------|------|------|
| ERROR | 심각한 오류 | 데이터베이스 연결 실패 |
| WARN | 경고 사항 | 메모리 사용량 높음 |
| INFO | 중요 정보 | 서버 시작, API 호출 |
| DEBUG | 디버깅 정보 | 메서드 실행, 변수 값 |
| TRACE | 상세 추적 | SQL 쿼리, 전체 요청/응답 |

## 로깅 사용법

### 1. Logger 생성
```kotlin
class MyService {
    // 자동으로 클래스 이름으로 Logger 생성
    private val logger = logger()
}
```

### 2. 기본 로깅
```kotlin
// 람다를 사용하여 성능 최적화 (로그 레벨이 비활성화된 경우 문자열 생성 안함)
logger.debug { "디버그 메시지" }
logger.info { "정보 메시지" }
logger.warn { "경고 메시지" }
logger.error { "에러 메시지" }

// 예외 포함
logger.error(exception) { "에러 발생" }
```

### 3. 실행 시간 측정
```kotlin
logger.logExecutionTime("database-query") {
    // 시간이 측정될 코드
    repository.findAll()
}
```

### 4. 구조화된 로깅 (MDC)
```kotlin
withLoggingContext(
    "userId" to userId,
    "operation" to "updateProfile"
) {
    logger.info { "사용자 프로필 업데이트" }
    // MDC에 userId와 operation이 자동으로 포함됨
}
```

### 5. 성능 로깅
```kotlin
// 별도 성능 로그 파일에 기록
PerformanceLogger.log(
    operation = "api-call",
    duration = 150,
    metadata = mapOf(
        "endpoint" to "/api/v1/characters",
        "method" to "GET"
    )
)
```

## 로그 패턴

### 콘솔 출력 (색상 포함)
```
2024-01-15 14:23:45.123 INFO  12345 --- [reactor-http-nio-2] c.g.t.service.CharacterService : 🎮 Creating new character: Hero
```

### 파일 출력
```
2024-01-15 14:23:45.123 INFO  12345 --- [reactor-http-nio-2] com.gijun.textrpg.service.CharacterService : Creating new character: Hero
```

## 로깅 모범 사례

### 1. 이모지 사용 (가독성 향상)
```kotlin
logger.info { "🎮 게임 시작" }
logger.warn { "⚠️ 경고: 메모리 사용량 높음" }
logger.error { "❌ 에러 발생" }
logger.info { "✅ 작업 완료" }
```

### 2. 적절한 로그 레벨 선택
- **DEBUG**: 개발 중 디버깅 정보
- **INFO**: 중요한 비즈니스 이벤트
- **WARN**: 잠재적 문제나 비정상적 상황
- **ERROR**: 에러 및 예외 상황

### 3. 민감한 정보 제외
```kotlin
// 나쁜 예
logger.info { "로그인: username=$username, password=$password" }

// 좋은 예
logger.info { "로그인 성공: username=$username" }
```

### 4. 성능 고려
```kotlin
// 나쁜 예 (항상 문자열 생성)
logger.debug("복잡한 객체: " + expensiveOperation())

// 좋은 예 (DEBUG 레벨이 활성화된 경우만 실행)
logger.debug { "복잡한 객체: ${expensiveOperation()}" }
```

## 환경별 설정

### 개발 환경 (local)
```yaml
logging:
  level:
    com.gijun.textrpg: DEBUG
    org.springframework.r2dbc: DEBUG  # SQL 쿼리 보기
```

### 운영 환경 (prod)
```yaml
logging:
  level:
    root: WARN
    com.gijun.textrpg: INFO
  file:
    path: /var/log/textrpg
```

## 로그 모니터링

### 로그 확인
```bash
# 실시간 로그 확인
tail -f logs/textrpg.log

# 특정 날짜 로그 확인
zcat logs/textrpg-2024-01-15.1.log.gz | less
```

### 로그 검색
```bash
# 특정 사용자 ID로 검색
grep "userId=12345" logs/textrpg.log

# 에러 수준 로그만 검색
grep "ERROR" logs/textrpg.log
```

## 문제 해결

로깅 관련 문제가 발생하면 [LOGGING_TROUBLESHOOTING.md](./LOGGING_TROUBLESHOOTING.md)를 참조하세요.

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
