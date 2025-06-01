# TextRPG API 테스트 가이드 (인증 시스템 포함)

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

## 2. 인증 API 테스트

### 사용자 등록
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 사용자 로그인 (JWT 토큰 발급)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**응답에서 토큰을 복사하세요:**
```json
{
  "id": "...",
  "username": "testuser",
  "email": "test@example.com",
  "roles": ["USER"],
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwic3ViIjoidGVzdHVzZXIiLCJpYXQiOjE2...",
  "expiresIn": 86400000
}
```

### 현재 사용자 정보 조회
```bash
# 위에서 받은 토큰을 사용
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwic3ViIjoidGVzdHVzZXIiLCJpYXQiOjE2..."

curl -X GET http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

## 3. 캐릭터 API 테스트 (인증 필요)

### 캐릭터 생성 (JWT 토큰 필요)
```bash
curl -X POST http://localhost:8080/api/v1/characters \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "MyHero"
  }'
```

### 캐릭터 조회
```bash
curl -X GET http://localhost:8080/api/v1/characters/{CHARACTER_ID} \
  -H "Authorization: Bearer $TOKEN"
```

### 모든 캐릭터 조회
```bash
curl -X GET http://localhost:8080/api/v1/characters \
  -H "Authorization: Bearer $TOKEN"
```

## 4. 기본 관리자 계정으로 테스트

프로젝트에는 기본 관리자 계정이 있습니다:
- Username: `admin`
- Password: `admin123`
- Roles: `["USER", "ADMIN"]`

### 관리자 로그인
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

## 5. 인증 없이 접근 가능한 엔드포인트

```bash
# 헬스 체크
curl http://localhost:8080/api/v1/health

# API 문서
curl http://localhost:8080/api-docs

# Swagger UI (브라우저에서)
http://localhost:8080/swagger-ui.html
```

## 6. 오류 응답 테스트

### 잘못된 로그인
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "wronguser",
    "password": "wrongpass"
  }'
```

### 토큰 없이 보호된 엔드포인트 접근
```bash
curl -X POST http://localhost:8080/api/v1/characters \
  -H "Content-Type: application/json" \
  -d '{
    "name": "UnauthorizedHero"
  }'
```

### 잘못된 토큰으로 접근
```bash
curl -X GET http://localhost:8080/api/v1/characters \
  -H "Authorization: Bearer invalid_token"
```

## 7. JWT 토큰 디버깅

토큰의 내용을 확인하려면 [jwt.io](https://jwt.io)에서 디코드할 수 있습니다.

## 8. 데이터베이스 상태 확인

```bash
# MySQL 접속
docker exec -it textrpg-mysql mysql -u root -p textrpg

# 사용자 테이블 확인
SELECT id, username, email, roles, is_active FROM users;

# 캐릭터 테이블 확인
SELECT id, name, user_id FROM characters;
```
