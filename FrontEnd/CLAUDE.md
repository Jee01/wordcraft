# WordCraft AI — Frontend 작업 기록

## 프로젝트 개요

**WordCraft AI**는 AI 기반 맞춤형 영단어 학습 플랫폼입니다.  
사용자가 선택한 태그(수능, 토익, 토플 등)에 맞는 단어 뜻·예문을 AI가 자동 생성하고,
단어장을 커뮤니티에서 공유하며 함께 학습하는 웹 애플리케이션(SPA)입니다.

- **기획서 참조:** `C:\Users\jkm28\OneDrive\Desktop\WordCraft_AI_기획서.docx`
- **기술 스택:** HTML5 / CSS3 / Vanilla JS (ES2022+), SPA 구조 (Hash Router)
- **백엔드 연동:** Spring Boot REST API (`/api/auth/login`, `/api/auth/register` 등)

---

## 파일 구조

```
C:\study\wordcraft\
└── src\main\resources\
    ├── style.css           # 공통 스타일 (다크/라이트 테마 변수 포함)
    ├── main.js             # 공통 JS (Hash Router, 햄버거 메뉴, 스크롤 reveal 등)
    └── static\             # ⚠️ html\ → static\ 로 폴더명 변경됨 (Spring Boot 정적 리소스 규칙)
        ├── index.html      # 메인 랜딩 페이지
        ├── login.html      # 로그인 페이지
        └── register.html   # 회원가입 페이지
```

> **CSS/JS 경로 주의:** `style.css`, `main.js`는 `static/` 폴더 안에 위치해야 하며, HTML에서 `style.css`, `main.js`로 참조.  
> Spring Boot는 `src/main/resources/static/`만 정적 리소스로 서빙하므로, 그 외 위치에 두면 CSS/JS가 로드되지 않음.  
> `login.html`, `register.html`에도 `<script src="main.js"></script>` 포함 필요.

---

## 완료 작업 목록

### 1. 메인 랜딩 페이지 (`index.html`)

**구성 섹션:**

| 섹션 | 내용 |
|---|---|
| Hero | 메인 타이틀, 플로팅 단어 카드 (4초마다 자동 순환), 서비스 통계 |
| 핵심 기능 | 6개 Feature Card (태그 AI 생성, 멀티 입력, AI Key 직접 사용, 커뮤니티, 테스트, 품사 분류) |
| 사용법 | 3단계 Step 가이드 (태그 입력 → AI 생성 → 학습·공유) |
| 지원 태그 | 수능·토익·토플·텝스·IELTS·일상회화·비즈니스·학술 + 커스텀 태그 |
| 지원 AI | OpenAI / Anthropic(추천) / Google 카드 |
| 커뮤니티 | 샘플 공개 단어장 3개 |
| CTA + Footer | 회원가입 유도, 링크 모음 |

**주요 인터랙션 (main.js):**
- 단어 카드 자동 순환: `implement → significant → constitute` (4초 간격, AI 생성 애니메이션 포함)
- 스크롤 진입 Reveal 애니메이션 (`IntersectionObserver`)
- 태그 아이템 클릭 토글
- 햄버거 메뉴 (모바일)
- 네브 스크롤 shadow

---

### 2. 로그인 페이지 (`login.html`)

- 이메일 / 비밀번호 입력 + 실시간 유효성 검사
- 로그인 유지 체크박스, 비밀번호 찾기 링크
- Google / GitHub 소셜 로그인 버튼 (OAuth placeholder)
- 우측 데코 패널: 샘플 단어 카드 (`establish`)
- 백엔드 연동 포인트: `POST /api/auth/login`

---

### 3. 회원가입 페이지 (`register.html`)

- 닉네임 / 이메일 / 비밀번호 / 비밀번호 확인 입력 + 실시간 유효성 검사
- **비밀번호 강도 바** (5단계: 매우 약함 → 매우 강함, 색상 + 레이블)
- 전체 동의 체크박스 (개별 약관 자동 연동)
- Google / GitHub 소셜 가입 버튼
- 우측 데코 패널: 서비스 특징 4가지 + 통계 카드
- **`POST /api/auth/register` 실제 API 연동 완료** (아래 참고)

---

### 4. 회원가입 API 연동 (`POST /api/auth/register`)

```
유효성 검사 통과
    ↓
버튼 비활성화 + "처리 중..." 표시
    ↓
fetch('POST /api/auth/register', { nickname, email, password })
    ↓
성공(2xx)  → login.html?registered=true 로 이동
실패(4xx)  → .api-error 박스에 서버 message 표시
           → code === 'EMAIL_DUPLICATE' 이면 이메일 필드 빨간 테두리 + 안내 문구
    ↓
finally → 버튼 다시 활성화 + 텍스트 복원
```

**서버 에러 응답 예상 형식 (백엔드와 맞춰야 함):**
```json
{ "message": "이미 사용 중인 이메일입니다.", "code": "EMAIL_DUPLICATE" }
```

**추가된 UI 요소:**
- `id="submitBtn"` — 로딩 중 `disabled` + 텍스트 변경
- `.api-error` — 서버 에러 메시지 표시 박스 (빨간 배경, 기본 hidden)

---

### 5. 다크 / 라이트 모드 토글

- 위치: 네브바 우상단 (로그인·시작하기 버튼 왼쪽)
- 다크 모드: 🌙 아이콘 표시 (기본값)
- 라이트 모드: ☀️ 아이콘 표시
- `localStorage('wc-theme')` 에 저장 → 새로고침 후에도 유지
- CSS는 `html.light { --bg: ...; }` 변수 오버라이드 방식
- 토글 로직은 `index.html` 하단 인라인 IIFE `<script>`에서 처리 (main.js와 분리)

**주의 — 과거 버그 및 수정 이력:**

1. **CSS 경로 오류** (`../../../style.css` → `../style.css`)  
   `static/` 폴더 기준 3단계 위에는 `style.css`가 없어 CSS 자체가 로드되지 않았음.  
   파일을 직접 열 때 스타일 전혀 미적용 → `../style.css`로 수정.

3. **`style.css` / `main.js` 서빙 불가 (2026-06-05 수정)**  
   `style.css`, `main.js`를 `src/main/resources/`에 두고 `../style.css`로 참조 → Spring Boot가 `static/` 외부 파일을 서빙하지 않아 CSS/JS 전혀 미적용.  
   → 두 파일을 `src/main/resources/static/`으로 이동, HTML 경로를 `style.css` / `main.js`로 수정.  
   → `login.html`, `register.html`에 누락된 `<script src="main.js"></script>` 추가.

2. **`const` 중복 선언 오류**  
   `main.js`와 인라인 `<script>` 모두 `const html = document.documentElement` 선언.  
   같은 전역 스코프에서 `const` 중복 → SyntaxError → 인라인 스크립트 실행 중단 → 클릭 핸들러 미등록.  
   → `main.js`에서 테마 토글 블록 완전 제거, 인라인 스크립트(IIFE)만 유지.

---

### 6. Hash Router (`main.js`)

`main.js` 최상단에 IIFE로 Hash Router 구현. `index.html`에서 각 페이지로 이동 가능.

```js
const ROUTES = {
  '#/login':    'login.html',
  '#/register': 'register.html',
};
```

**동작 방식:**
- 페이지 최초 진입 시 현재 hash 확인 → 매핑된 페이지로 즉시 이동
- `hashchange` 이벤트 발생 시 동일하게 처리
- `#features`, `#how`, `#community` 등 앵커 스크롤용 hash는 ROUTES 미등록 → 무시(스크롤 유지)

**향후 페이지 추가 시 ROUTES에 한 줄 추가:**
```js
'#/dashboard': 'dashboard.html',
'#/vocab/new': 'vocab-new.html',
```

---

## 디자인 시스템 (style.css)

### 컬러 팔레트

| 변수 | 다크 | 라이트 |
|---|---|---|
| `--bg` | `#0d0d0f` | `#f5f5f7` |
| `--bg-2` | `#111114` | `#ebebee` |
| `--bg-3` | `#17171b` | `#ffffff` |
| `--text` | `#e8e8f0` | `#111118` |
| `--text-2` | `#a0a0b8` | `#4a4a60` |
| `--accent` | `#6c63ff` | `#5a52e8` |

### 주요 클래스

```
.btn--primary      # 보라 계열 채움 버튼
.btn--ghost        # 테두리만 있는 버튼
.btn--lg / .btn--sm
.container         # max-width: 1140px 중앙 정렬
.section--dark     # 배경색이 한 단계 어두운 섹션
.reveal / .visible # 스크롤 진입 애니메이션
.tag--toeic / --toefl / --csat / --noun  # 태그 색상
html.light { ... } # 라이트 모드 CSS 변수 오버라이드
```

---

## 향후 작업 예정 (미구현)

- `#/dashboard` — 내 단어장 목록, 최근 학습 현황
- `#/vocab/new` — 태그 선택 + 단어 입력 + AI 생성 UI
- `#/vocab/:id` — 단어장 상세 (조회·수정·삭제)
- `#/vocab/import` — 텍스트·이미지·파일 업로드 자동 변환
- `#/test/:vocabId` — 테스트 유형 선택 + 문제 풀기
- `#/test/:id/result` — 정답률·오답 분석 결과
- `#/community` — 공개 단어장 탐색·검색
- `#/settings` — 프로필, AI API Key 입력·관리
