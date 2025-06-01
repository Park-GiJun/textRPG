# TextRPG Frontend

스벨트킷(SvelteKit)으로 구현된 TextRPG 게임의 프론트엔드입니다.

## 🚀 빠른 시작

### 필수 요구사항
- Node.js 18+ 
- npm 또는 yarn

### 설치 및 실행

#### Windows 사용자
```bash
# PowerShell에서 (권장)
.\start.ps1

# 또는 Command Prompt에서
start.bat

# 또는 수동으로
npm install
npm run dev
```

#### Mac/Linux 사용자
```bash
npm install
npm run dev
```

### 접속
- 프론트엔드: http://localhost:3000
- 백엔드 API: http://localhost:8080/api (프록시)

## 🎮 주요 기능

### ✅ 구현된 기능
- 🔐 **사용자 인증**
  - 회원가입 (유효성 검사 포함)
  - 로그인/로그아웃
  - JWT 토큰 기반 인증

- 👤 **캐릭터 관리**
  - 캐릭터 생성 (커스텀 스탯 설정)
  - 캐릭터 목록 조회
  - 캐릭터 삭제
  - 캐릭터 선택

- 🎯 **게임 플레이**
  - 기본적인 텍스트 기반 게임 플레이
  - 상호작용 액션 시스템
  - 실시간 게임 로그
  - 캐릭터 상태 표시

### 🔄 기술적 특징
- **반응형 디자인**: 모바일부터 데스크톱까지 지원
- **실시간 상태 관리**: Svelte 스토어 기반
- **타입 안전성**: TypeScript 사용
- **API 통합**: 백엔드 REST API와 완전 연동
- **현대적 UI**: Tailwind CSS로 RPG 테마 구현

## 🛠 기술 스택

- **프레임워크**: SvelteKit
- **언어**: TypeScript
- **스타일링**: Tailwind CSS
- **상태 관리**: Svelte Stores
- **HTTP 클라이언트**: Fetch API
- **인증**: JWT (쿠키 저장)
- **빌드 도구**: Vite

## 📁 프로젝트 구조

```
frontend/
├── src/
│   ├── lib/
│   │   ├── components/          # 재사용 가능한 컴포넌트
│   │   │   ├── LoginForm.svelte       # 로그인 폼
│   │   │   ├── RegisterForm.svelte    # 회원가입 폼
│   │   │   ├── CharacterList.svelte   # 캐릭터 목록
│   │   │   ├── CreateCharacter.svelte # 캐릭터 생성
│   │   │   ├── GameView.svelte        # 게임 플레이
│   │   │   └── Notification.svelte    # 알림 컴포넌트
│   │   ├── api.ts               # API 서비스 레이어
│   │   └── stores.ts            # 전역 상태 관리
│   ├── routes/
│   │   ├── +layout.svelte       # 공통 레이아웃
│   │   └── +page.svelte         # 메인 페이지
│   ├── app.html                 # HTML 템플릿
│   ├── app.css                  # 전역 스타일
│   └── app.d.ts                 # 타입 정의
├── static/                      # 정적 파일
├── package.json
├── vite.config.js              # Vite 설정
├── svelte.config.js            # SvelteKit 설정
├── tailwind.config.js          # Tailwind 설정
└── tsconfig.json               # TypeScript 설정
```

## 🎨 UI/UX 특징

### 테마
- **다크 모드**: RPG 게임에 적합한 어두운 테마
- **판타지 폰트**: 'Cinzel' (제목), 'Orbitron' (본문)
- **색상 팔레트**: 
  - 골드 (#ffd700) - 강조 요소
  - 블루/퍼플 그라데이션 - 버튼
  - 클래스별 색상 (빨강/초록/파랑/노랑)

### 애니메이션
- 페이드인 전환 효과
- 호버 스케일 효과
- 부드러운 상태 전환
- 로딩 스피너 및 펄스 효과

## 🔧 개발 가이드

### 새로운 컴포넌트 추가
```svelte
<script lang="ts">
  // TypeScript 로직
</script>

<!-- HTML 마크업 -->
<div class="card">
  <h3 class="rpg-title">제목</h3>
  <button class="btn-primary">버튼</button>
</div>

<style>
  /* 컴포넌트별 스타일 */
</style>
```

### 상태 관리
```typescript
// stores.ts에서 새로운 스토어 추가
export const newStore = writable<Type>(initialValue);

// 컴포넌트에서 사용
import { newStore } from '$lib/stores';
$: $newStore // 반응형으로 구독
newStore.set(newValue) // 값 설정
```

### API 호출
```typescript
// api.ts에서 새로운 API 함수 추가
async function newApiCall(): Promise<ReturnType> {
  const response = await fetch(`${API_BASE_URL}/endpoint`, {
    headers: this.getAuthHeaders()
  });
  return this.handleResponse<ReturnType>(response);
}
```

## 🚧 향후 개발 계획

### 우선순위 높음
- [ ] WebSocket 연결 (실시간 멀티플레이어)
- [ ] 인벤토리 시스템 확장
- [ ] 전투 시스템 구현
- [ ] 스킬 사용 인터페이스

### 우선순위 중간  
- [ ] 캐릭터 프로필 상세 페이지
- [ ] 게임 설정 페이지
- [ ] 음효 및 배경음악
- [ ] 게임 저장/불러오기

### 우선순위 낮음
- [ ] 테마 변경 기능
- [ ] 다국어 지원
- [ ] 접근성 개선
- [ ] PWA 지원

## 🐛 알려진 이슈

1. **캐릭터 스트림 응답**: 백엔드에서 JSON 스트림으로 응답하는데, 파싱이 완벽하지 않을 수 있음
2. **토큰 만료**: JWT 토큰 자동 갱신 기능이 없음
3. **모바일 최적화**: 일부 UI 요소가 모바일에서 최적화되지 않음

## 📞 문제 해결

### 자주 묻는 질문

**Q: 의존성 설치 시 오류가 발생합니다**
A: Node.js 18+ 버전을 사용하고 있는지 확인하세요.

**Q: 백엔드 연결이 되지 않습니다**
A: 백엔드 서버가 localhost:8080에서 실행 중인지 확인하세요.

**Q: 로그인 후 토큰이 저장되지 않습니다**
A: 브라우저 쿠키 설정을 확인하고, 개발자 도구에서 쿠키를 확인하세요.

### 개발자 도구
- Chrome/Edge DevTools에서 Network 탭으로 API 호출 확인
- Application 탭에서 쿠키 및 저장소 확인
- Console에서 에러 메시지 확인

## 📄 라이선스

이 프로젝트는 백엔드와 동일한 라이선스를 따릅니다.
