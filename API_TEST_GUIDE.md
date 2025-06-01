# 캐릭터 생성 API 테스트 가이드 (캐시/이벤트 포함)

## 1. 환경 설정

### 인프라 실행
```bash
docker-compose up -d mysql redis kafka
```

### 로그 디렉토리 생성
```bash
mkdir logs
```

### 애플리케이션 실행
```bash
./gradlew bootRun
```

## 2. API 테스트

### 캐릭터 생성 (기본 스탯)
```bash
curl -X POST http://localhost:8080/api/v1/characters \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hero"
  }'
```

### 캐릭터 생성 (커스텀 스탯)
```bash
curl -X POST http://localhost:8080/api/v1/characters \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Warrior",
    "strength": 20,
    "dexterity": 10,
    "intelligence": 5,
    "luck": 8
  }'
```

### 캐릭터 조회 (캐시 확인)
```bash
# 첫 번째 조회 (DB에서 가져와서 캐시에 저장)
curl http://localhost:8080/api/v1/characters/{CHARACTER_ID}

# 두 번째 조회 (캐시에서 바로 가져옴 - 더 빠름)
curl http://localhost:8080/api/v1/characters/{CHARACTER_ID}
```

### 모든 캐릭터 조회
```bash
curl http://localhost:8080/api/v1/characters
```

### 캐릭터 수정
```bash
curl -X PUT http://localhost:8080/api/v1/characters/{CHARACTER_ID} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "NewName"
  }'
```

### 캐릭터 삭제
```bash
curl -X DELETE http://localhost:8080/api/v1/characters/{CHARACTER_ID}
```

## 3. 캐시 & 이벤트 시스템 확인

### 헬스 체크 (캐시 상태 포함)
```bash
curl http://localhost:8080/api/v1/health
```

**응답 예시:**
```json
{
  "status": "UP",
  "timestamp": "2024-01-15T14:30:00",
  "services": {
    "api": "UP",
    "database": "UP",
    "redis": "UP",
    "cache": "UP"
  }
}
```

### 로그에서 이벤트 확인
```bash
# 캐릭터 생성 시 로그 확인
tail -f logs/textrpg.log | grep -E "(Creating character|Character created|환영합니다)"

# 캐시 로그 확인
tail -f logs/textrpg.log | grep -E "(Cache HIT|Cache MISS|Character cached)"

# 이벤트 발행 로그 확인
tail -f logs/textrpg.log | grep -E "(Published event|Published notification)"
```

### Redis 캐시 직접 확인
```bash
# Redis 접속
docker exec -it textrpg-redis redis-cli

# 캐릭터 캐시 키 확인
KEYS character:*

# 특정 캐릭터 캐시 조회
GET character:{CHARACTER_ID}

# 이름으로 캐릭터 ID 조회
GET character:name:{CHARACTER_NAME}
```

### Kafka 이벤트 확인
```bash
# Kafka 컨테이너 접속
docker exec -it textrpg-kafka bash

# character-events 토픽 메시지 확인
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic character-events --from-beginning

# notifications 토픽 메시지 확인
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic notifications --from-beginning
```

## 4. 응답 예시

### 캐릭터 생성 성공 응답 (201 Created)
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Hero",
  "level": 1,
  "health": {
    "current": 105,
    "max": 105,
    "percentage": 100.0
  },
  "stats": {
    "strength": 10,
    "dexterity": 10,
    "intelligence": 10,
    "luck": 5,
    "totalPower": 35
  },
  "createdAt": "2024-01-15T14:30:00",
  "updatedAt": "2024-01-15T14:30:00"
}
```

### 캐시 성능 확인
캐릭터를 여러 번 조회하면서 응답 시간을 비교해보세요:
- 첫 번째 조회: 데이터베이스 접근 (더 느림)
- 두 번째 조회: 캐시에서 조회 (더 빠름)

## 5. 이벤트 시스템 동작 확인

### 캐릭터 생성 시 발생하는 이벤트들
1. **캐릭터 생성 이벤트** → `character-events` 토픽
2. **알림 메시지** → `notifications` 토픽
3. **Slack 알림** (webhook URL 설정 시)
4. **캐시 저장**

### 캐릭터 삭제 시 발생하는 이벤트들
1. **캐릭터 삭제 이벤트** → `character-events` 토픽
2. **캐시 무효화**
3. **알림 메시지** → `notifications` 토픽

## 6. 성능 테스트

### 캐시 성능 비교 스크립트
```bash
#!/bin/bash
CHARACTER_ID="your-character-id"

echo "첫 번째 조회 (DB에서):"
time curl -s http://localhost:8080/api/v1/characters/$CHARACTER_ID > /dev/null

echo "두 번째 조회 (캐시에서):"
time curl -s http://localhost:8080/api/v1/characters/$CHARACTER_ID > /dev/null

echo "세 번째 조회 (캐시에서):"
time curl -s http://localhost:8080/api/v1/characters/$CHARACTER_ID > /dev/null
```

## 7. 트러블슈팅

### Redis 연결 문제
```bash
# Redis 상태 확인
docker ps | grep redis

# Redis 로그 확인
docker logs textrpg-redis
```

### Kafka 연결 문제
```bash
# Kafka 상태 확인
docker ps | grep kafka

# Kafka 토픽 목록 확인
docker exec textrpg-kafka kafka-topics.sh --bootstrap-server localhost:9092 --list
```

### 캐시 초기화
```bash
# 모든 캐릭터 캐시 삭제
docker exec -it textrpg-redis redis-cli FLUSHDB
```

## 8. 모니터링

### Swagger UI
http://localhost:8080/swagger-ui.html

### Actuator 엔드포인트
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:8080/actuator/prometheus

### 로그 모니터링
```bash
# 실시간 로그 확인
tail -f logs/textrpg.log

# 에러 로그만 확인
tail -f logs/textrpg.log | grep ERROR

# 성능 로그 확인 (설정된 경우)
tail -f logs/textrpg-performance.log
```
