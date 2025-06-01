# TextRPG - 헥사고날 아키텍처 기반 텍스트 RPG 게임

## 프로젝트 소개
Spring Boot 3.5.0과 Kotlin을 활용한 헥사고날 아키텍처 기반의 텍스트 RPG 게임 서버입니다.
Reactive Programming (WebFlux + Coroutines)을 적용하여 높은 동시성과 성능을 제공합니다.

## 기술 스택

### Backend
- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.0
- **Reactive**: Spring WebFlux + Kotlin Coroutines
- **Build Tool**: Gradle (Kotlin DSL)
- **Java Version**: 21

### Database & Cache
- **Primary DB**: MySQL with R2DBC
- **Cache**: Redis (Reactive)
- **Search**: Elasticsearch
- **Migration**: Flyway

### Messaging & Real-time
- **Message Broker**: Kafka
- **Real-time**: WebSocket

### Monitoring & Security
- **Security**: Spring Security + JWT
- **Error Tracking**: Sentry
- **Notification**: Slack
- **Metrics**: Prometheus + Micrometer
- **Tracing**: Zipkin

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
│   │   ├── in/             # 인바운드 포트
│   │   └── out/            # 아웃바운드 포트
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
```

## 시작하기

### 필수 요구사항
- JDK 21+
- Docker & Docker Compose
- MySQL 8.0+
- Redis 7.0+

### 환경 설정

1. 필요한 인프라 실행
```bash
docker-compose up -d mysql redis elasticsearch kafka
```

2. 로그 디렉토리 생성
```bash
mkdir logs  # Windows: md logs
```

3. 환경 변수 설정
```bash
export DB_USERNAME=root
export DB_PASSWORD=password
export JWT_SECRET=your-secret-key
export SENTRY_DSN=your-sentry-dsn
export SLACK_WEBHOOK_URL=your-webhook-url
```

4. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 로깅 설정 선택
프로젝트는 두 가지 로깅 설정을 제공합니다:

1. **기본 설정** (권장): Spring Boot 기본 로깅 사용
   - 별도 설정 불필요
   - `application.yml`의 설정만으로 충분

2. **고급 설정**: Logback XML 사용
   - `logback-spring.xml` 파일 사용
   - 환경별 상세 설정 가능
   - 문제 발생 시 `LOGGING_TROUBLESHOOTING.md` 참조

### API 문서
애플리케이션 실행 후 아래 URL에서 API 문서를 확인할 수 있습니다:
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## 주요 기능

### 게임 기능
- 캐릭터 생성 및 관리
- 스킬 시스템
- 스토리 진행
- 실시간 멀티플레이어

### 기술적 특징
- **헥사고날 아키텍처**: 유연하고 테스트 가능한 구조
- **Reactive Programming**: 높은 동시성 처리
- **이벤트 드리븐**: Kafka를 통한 비동기 이벤트 처리
- **실시간 통신**: WebSocket을 통한 실시간 게임플레이

## 개발 가이드

### 코딩 컨벤션
- 불변성 우선 (val > var)
- Null Safety 활용
- suspend fun 우선 사용
- Flow 사용 (스트리밍 데이터)

### 테스트
```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests "com.gijun.textrpg.*"
```

### 빌드
```bash
# JAR 파일 생성
./gradlew build

# Docker 이미지 빌드
docker build -t textrpg:latest .
```

## 기여하기
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 라이선스
이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 문의사항
프로젝트에 대한 문의사항이나 버그 리포트는 Issues 탭을 이용해주세요.
