# 사용자 인증 & 캐릭터 생성 API 테스트 가이드

## 새로운 플로우: 회원가입 → 로그인 → 캐릭터 생성

### 1. 회원가입

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testplayer",
    "email": "test@example.com",
    "password": "password123"
  }'
```

**응답 예시:**
```json
{
  "message": "회원가입이 완료되었습니다.",
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "username": "testplayer",
    "email": "test@example.com",
    "role": "PLAYER",
    "createdAt": "2024-01-15T14:30:00"
  }
}
```

### 2. 로그인

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testplayer",
    "password": "password123"
  }'
```

**응답 예시:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "로그인이 완료되었습니다.",
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "username": "testplayer",
    "email": "test@example.com",
    "role": "PLAYER",
    "createdAt": "2024-01-15T14:30:00"
  }
}
```

⚠️ **중요**: 토큰을 저장해두세요! 캐릭터 생성 시 필요합니다.

### 3. 캐릭터 생성 (JWT 토큰 필요)

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."  # 로그인에서 받은 토큰

curl -X POST http://localhost:8080/api/v1/characters \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "MyHero",
    "strength": 15,
    "dexterity": 12,
    "intelligence": 8,
    "luck": 10
  }'
```

### 4. 내 캐릭터 목록 조회

```bash
curl -X GET http://localhost:8080/api/v1/characters/my \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Swagger UI 사용법

1. http://localhost:8080/swagger-ui.html 접속
2. **먼저 `/api/v1/auth/register`로 회원가입**
3. **`/api/v1/auth/login`으로 로그인 후 토큰 복사**
4. **Authorize 버튼 클릭**
5. **`Bearer {토큰}` 입력** (예: `Bearer eyJhbGciOiJIUzI1NiIs...`)
6. **이제 캐릭터 API 사용 가능!**

## 주요 변경사항

### 1. 캐릭터는 이제 사용자에게 속함
- 각 캐릭터는 `userId`를 가짐
- 같은 사용자 내에서만 캐릭터 이름 중복 불가
- 다른 사용자는 같은 이름의 캐릭터 생성 가능

### 2. JWT 토큰 기반 인증
- 모든 캐릭터 관련 API는 JWT 토큰 필요
- 토큰에서 사용자 ID 추출하여 권한 확인

### 3. 새로운 엔드포인트
- `POST /api/v1/auth/register` - 회원가입
- `POST /api/v1/auth/login` - 로그인
- `GET /api/v1/auth/profile` - 프로필 조회
- `GET /api/v1/characters/my` - 내 캐릭터 목록

## 에러 처리

### 회원가입 실패
```json
{
  "status": 409,
  "error": "Conflict", 
  "message": "Username 'testplayer' is already taken"
}
```

### 로그인 실패
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password"
}
```

### 토큰 없이 캐릭터 생성 시도
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Authorization header is required"
}
```

## 게임 시나리오 테스트

### 1. 두 명의 플레이어 시뮬레이션
```bash
# 플레이어 1
curl -X POST http://localhost:8080/api/v1/auth/register \
  -d '{"username": "player1", "email": "player1@test.com", "password": "pass123"}'

curl -X POST http://localhost:8080/api/v1/auth/login \
  -d '{"username": "player1", "password": "pass123"}'

# 플레이어 2  
curl -X POST http://localhost:8080/api/v1/auth/register \
  -d '{"username": "player2", "email": "player2@test.com", "password": "pass123"}'

curl -X POST http://localhost:8080/api/v1/auth/login \
  -d '{"username": "player2", "password": "pass123"}'
```

### 2. 같은 이름 캐릭터 생성 (각자의 계정에서)
```bash
# 둘 다 "Hero"라는 이름으로 캐릭터 생성 가능!
curl -X POST http://localhost:8080/api/v1/characters \
  -H "Authorization: Bearer $TOKEN1" \
  -d '{"name": "Hero"}'

curl -X POST http://localhost:8080/api/v1/characters \
  -H "Authorization: Bearer $TOKEN2" \
  -d '{"name": "Hero"}'
```

이제 실제 RPG 게임처럼 사용자별로 캐릭터를 관리할 수 있습니다! 🎮
