# TextRPG 헥사고날 아키텍처 가이드 (Reactive Stack)

## 프로젝트 구조

```
src/main/kotlin/com/gijun/textrpg/
├── domain/                    # 도메인 계층 (비즈니스 로직)
│   ├── character/            # 캐릭터 도메인
│   ├── skill/               # 스킬 도메인
│   ├── story/               # 스토리 도메인
│   └── game/                # 게임 상태 도메인
├── application/              # 애플리케이션 계층 (유스케이스)
│   ├── port/
│   │   ├── in/             # 인바운드 포트 (유스케이스 인터페이스)
│   │   └── out/            # 아웃바운드 포트 (외부 시스템 인터페이스)
│   └── service/            # 유스케이스 구현
├── adapter/                  # 어댑터 계층 (외부 연동)
│   ├── in/
│   │   ├── web/           # REST API
│   │   └── websocket/     # WebSocket
│   └── out/
│       ├── persistence/    # R2DBC/MySQL
│       ├── cache/         # Redis Reactive
│       ├── messaging/     # Kafka
│       └── notification/  # Slack
└── configuration/           # 설정 클래스
    ├── SecurityTemplate.kt  # Spring Security & JWT
    ├── SentryConfiguration.kt
    ├── SlackConfiguration.kt
    └── MonitoringConfiguration.kt
```

## 계층별 역할

### 1. Domain Layer (도메인 계층)
- **목적**: 순수한 비즈니스 로직
- **특징**: 
  - 외부 프레임워크 의존성 없음
  - 불변성 중심 설계
  - Rich Domain Model
- **주요 컴포넌트**:
  - Entity: 고유 식별자를 가진 도메인 객체
  - Value Object: 불변 객체
  - Domain Service: 도메인 로직
  - Domain Event: 비즈니스 이벤트

### 2. Application Layer (애플리케이션 계층)
- **목적**: 유스케이스 구현 및 조율
- **특징**:
  - 트랜잭션 경계 설정
  - 도메인 객체 조율
  - 포트 정의
  - **Coroutines 기반 비동기 처리**
- **주요 컴포넌트**:
  - Inbound Port: 외부→애플리케이션 인터페이스
  - Outbound Port: 애플리케이션→외부 인터페이스
  - Application Service: 유스케이스 구현체

### 3. Adapter Layer (어댑터 계층)
- **목적**: 외부 시스템과의 연결
- **특징**:
  - 기술 종속적 코드
  - 도메인 모델 ↔ 외부 모델 변환
  - 각 어댑터는 독립적으로 교체 가능
  - **Reactive Streams 지원**
- **주요 컴포넌트**:
  - Inbound Adapter: REST, WebSocket, GraphQL
  - Outbound Adapter: R2DBC, Redis Reactive, Kafka, Slack

## 주요 기술 스택 (Reactive)

### Backend
- **Language**: Kotlin
- **Framework**: Spring Boot 3.5.0
- **Reactive**: Spring WebFlux + Coroutines
- **Build Tool**: Gradle (Kotlin DSL)

### Database & Cache
- **Primary DB**: MySQL with R2DBC
- **Cache**: Redis (Reactive)
- **Search**: Elasticsearch
- **Migration**: Flyway (JDBC URL 사용)

### Messaging & Event
- **Message Broker**: Kafka
- **Protocol**: WebSocket (실시간 통신)

### Security
- **Authentication**: JWT
- **Framework**: Spring Security

### Monitoring & Logging
- **Error Tracking**: Sentry
- **Notification**: Slack
- **Metrics**: Prometheus + Micrometer
- **Tracing**: Zipkin
- **Logging**: Kotlin-logging (SLF4J)

### API Documentation
- **Tool**: SpringDoc OpenAPI (Swagger)

## Reactive 프로그래밍 가이드

### 1. Coroutines 사용
```kotlin
// 서비스 레이어
suspend fun findCharacter(id: String): Character? {
    return repository.findById(id).awaitFirstOrNull()
}

// 컨트롤러
@GetMapping("/{id}")
suspend fun getCharacter(@PathVariable id: String): ResponseEntity<CharacterDto> {
    val character = service.findCharacter(id)
    return ResponseEntity.ok(character.toDto())
}
```

### 2. Flow 사용
```kotlin
// 스트리밍 데이터
fun streamCharacters(): Flow<Character> {
    return repository.findAll().asFlow()
}

// 서버 전송 이벤트 (SSE)
@GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
fun streamEvents(): Flow<ServerSentEvent<GameEvent>> {
    return service.getGameEvents()
        .map { ServerSentEvent.builder(it).build() }
}
```

### 3. R2DBC 트랜잭션
```kotlin
@Transactional
suspend fun transferItem(fromId: String, toId: String, itemId: String) {
    // 트랜잭션 내에서 여러 작업 수행
    val from = characterRepository.findById(fromId).awaitFirst()
    val to = characterRepository.findById(toId).awaitFirst()
    
    // 아이템 이동 로직
    // ...
    
    characterRepository.save(from).awaitSingle()
    characterRepository.save(to).awaitSingle()
}
```

## 개발 가이드

### 1. 새로운 기능 추가 시
1. Domain 모델 정의 (Entity, Value Object)
2. Inbound Port 정의 (suspend fun 사용)
3. Outbound Port 정의 (Reactive 타입 반환)
4. Application Service 구현 (Coroutines)
5. Adapter 구현 (R2DBC, WebFlux)

### 2. 테스트 전략
- **Domain**: 단위 테스트 (순수 로직)
- **Application**: 통합 테스트 (MockK + Coroutines Test)
- **Adapter**: 통합 테스트 (TestContainers + R2DBC)
- **Architecture**: ArchUnit (아키텍처 규칙 검증)

### 3. 코딩 컨벤션
- 불변성 우선 (val > var)
- Null Safety 활용
- suspend fun 우선 (Reactive 타입보다)
- Flow 사용 (스트리밍 데이터)

### 4. 패키지 의존성 규칙
```
Domain → (의존하지 않음)
Application → Domain
Adapter → Application, Domain
Configuration → All Layers
```

## 실행 방법

### 로컬 환경 설정
```bash
# 1. 인프라 실행 (docker-compose.yml 필요)
docker-compose up -d mysql redis elasticsearch kafka

# 2. 애플리케이션 실행
./gradlew bootRun

# 3. API 문서 확인
http://localhost:8080/swagger-ui.html
```

### 환경 변수
```
DB_USERNAME=root
DB_PASSWORD=password
JWT_SECRET=your-secret-key
SENTRY_DSN=your-sentry-dsn
SLACK_WEBHOOK_URL=your-webhook-url
```

## 주요 엔드포인트

### REST API (Reactive)
- `POST /api/v1/auth/login` - 로그인
- `POST /api/v1/characters` - 캐릭터 생성
- `GET /api/v1/characters/{id}` - 캐릭터 조회
- `GET /api/v1/characters/stream` - 캐릭터 스트림 (SSE)

### WebSocket
- `ws://localhost:8080/ws/game` - 게임 실시간 통신

## 모니터링
- Health Check: `GET /actuator/health`
- Metrics: `GET /actuator/prometheus`
- API Docs: `GET /api-docs`

## R2DBC 관련 주의사항

1. **Lazy Loading 없음**: 연관 관계는 명시적으로 조인
2. **트랜잭션 범위**: @Transactional 사용 시 suspend fun 필수
3. **Connection Pool**: R2DBC pool 설정 최적화 필요
4. **JSON 타입**: 수동 매핑 필요 (MySQL JSON 컬럼)

## 성능 최적화 팁

1. **배치 처리**: Flux.buffer() 사용
2. **캐싱**: Redis Reactive 적극 활용
3. **비동기 처리**: Coroutines + Flow
4. **백프레셔**: 적절한 buffer 크기 설정
