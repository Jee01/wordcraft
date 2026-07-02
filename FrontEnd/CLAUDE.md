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
- **`POST /api/auth/login` 실제 API 연동 완료** (아래 참고)

---

### 5-1. 로그인 API 연동 (`POST /api/auth/login`)

```
유효성 검사 통과
    ↓
버튼 비활성화 + "처리 중..." 표시
    ↓
fetch('POST /api/auth/login', { email, password })
    ↓
성공(2xx)  → accessToken / refreshToken 을 localStorage 저장 → index.html 이동
실패(4xx)  → .api-error 박스에 서버 message 표시
네트워크 오류 → "서버와 통신할 수 없습니다." 안내
    ↓
finally → 버튼 다시 활성화 + 텍스트 복원
```

**요청 바디 (LoginRequestDTO):**
```json
{ "email": "user@example.com", "password": "password123" }
```

**응답 바디 (TokenResponseDTO):**
```json
{ "accessToken": "...", "refreshToken": "..." }
```

**추가된 UI 요소:**
- `id="submitBtn"` — 로딩 중 `disabled` + 텍스트 변경
- `.api-error` — 서버 에러 메시지 표시 박스 (빨간 배경, 기본 hidden)

---

### 5-2. 이메일 유효성 검사 정규식 강화 (`login.html`)

**변경 전:** `/^[^\s@]+@[^\s@]+\.[^\s@]+$/`  
→ 공백·@ 제외 모든 문자 허용, TLD 1자리도 통과하는 문제

**변경 후:** `/^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$/`  
→ 허용 문자 명시(`a-z A-Z 0-9 . _ % + -`), TLD **최소 2자리** 강제

| 입력 예시 | 결과 |
|---|---|
| `jkm2821@` | 실패 (도메인 없음) |
| `jkm2821@gmail` | 실패 (점 없음) |
| `jkm2821@gmail.c` | 실패 (TLD 1자리) |
| `jkm2821@gmail.co` | 통과 |
| `jkm2821@gmail.com` | 통과 |

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

## 미구현 → 구현 완료 (2026-06-07)

이전에 "향후 작업 예정"이었던 8개 페이지가 모두 구현됨.

### 신규 구현 페이지

| 파일 | 라우트 | 주요 기능 |
|---|---|---|
| `dashboard.html` | `#/dashboard` | 내 단어장 그리드, 학습 통계 바, 최근 활동 내역 |
| `vocab-new.html` | `#/vocab/new` | 태그 멀티 선택, 단어 입력(미리보기), AI 제공자 선택, `POST /api/vocab/generate` 연동 |
| `vocab.html` | `#/vocab/:id` | 단어 카드 그리드, 검색·필터, 학습 완료 토글, 공개/비공개 전환, 삭제 모달 |
| `vocab-import.html` | `#/vocab/import` | 텍스트·이미지·파일 3탭, 드래그앤드롭, `POST /api/vocab/import/*` 연동 |
| `test.html` | `#/test/:vocabId` | 5가지 테스트 유형(뜻 맞추기·빈칸·플래시카드·스펠링·연결), 진행 바, 건너뛰기 |
| `test-result.html` | `#/test/:id/result` | 점수 원형, 정답/오답/건너뜀 통계, 오답 단어 목록, `POST /api/test/history` 저장 |
| `community.html` | `#/community` | 검색바, 태그 필터, 인기/최신/단어수 정렬, 좋아요 토글, 단어장 복사 |
| `settings.html` | `#/settings` | 프로필 수정, AI API Key 관리(localStorage만 저장), 학습 설정 토글, 비밀번호 변경, 위험 구역 |

### Hash Router 업데이트 (`main.js`)

정적 라우트 6개 + 동적 라우트 패턴 매칭 추가:

```js
const ROUTES = {
  '#/login':        'login.html',
  '#/register':     'register.html',
  '#/dashboard':    'dashboard.html',
  '#/vocab/new':    'vocab-new.html',
  '#/vocab/import': 'vocab-import.html',
  '#/community':    'community.html',
  '#/settings':     'settings.html',
};
// 동적 패턴
// #/vocab/:id      → vocab.html?id=:id
// #/test/:vocabId  → test.html?vocabId=:id
// #/test/:id/result → test-result.html?vocabId=:id
```

### main.js 버그 수정

Word card cycling 코드에 null 체크 추가.  
비인덱스 페이지에서 `genBar.classList.add('active')` 호출 시 발생하던 오류 수정:

```js
function cycleWord() {
  if (!card || !genBar) return;  // ← 추가됨
  ...
}
```

### 공통 설계 원칙

- 모든 보호 페이지: 진입 시 `accessToken` 체크 → 없으면 `login.html` 리다이렉트
- API 실패 시 샘플 데이터 폴백 → 백엔드 없이도 UI 확인 가능
- `style.css` 디자인 시스템 계승 (다크/라이트, 컬러 변수, 공통 클래스)
- 테스트 결과는 `sessionStorage('testResults')`로 `test.html` → `test-result.html` 전달

---

## 디자인 미리보기 전용 파일 (`FrontEnd/`)

`C:\study\wordcraft\FrontEnd\` 폴더에 동일 페이지의 **인증 없는 버전** 생성 (2026-06-07).  
브라우저에서 HTML 파일을 직접 열어 로그인 없이 디자인 확인 가능.

**생성 방법:** `src/main/resources/static/` 파일을 복사 후 3가지 치환:

| 항목 | 원본 | FrontEnd 버전 |
|---|---|---|
| CSS 경로 | `href="style.css"` | `href="../src/main/resources/static/style.css"` |
| JS 경로 | `src="main.js"` | `src="../src/main/resources/static/main.js"` |
| 인증 체크 | `if (!token) { window.location.href = 'login.html'; return; }` | `/* design-preview: auth check removed */` |
| logout 함수 | `window.location.href = 'login.html'` | `/* design-preview: no redirect */` |
| 401 처리 | `if (res.status === 401) { logout(); return; }` | `/* 401 skipped in design preview */` |

**포함 파일 (11개):**  
`index.html` · `login.html` · `register.html` · `dashboard.html` · `vocab-new.html` · `vocab.html` · `vocab-import.html` · `test.html` · `test-result.html` · `community.html` · `settings.html`

> **주의:** FrontEnd 파일은 디자인 확인 전용입니다. 실제 기능 수정은 `src/main/resources/static/` 파일에서 진행 후 FrontEnd를 재생성해야 합니다.

---

## 수동 단어 입력 페이지 (2026-06-12)

### 신규 파일

| 파일 | 위치 | 설명 |
|---|---|---|
| `manual-vocab-add.html` | `src/main/resources/static/` | 실서비스용 (인증 포함) |
| `design_only_view.html` | `FrontEnd/` | 디자인 미리보기 전용 (인증 없음) |

### 기능 개요

AI 없이 사용자가 단어·뜻·예문·메모를 직접 입력하는 단어장 생성 페이지. `vocab-new.html`(AI 자동 생성)과 대응되는 수동 입력 방식.

**구성 섹션:**

| 섹션 | 내용 |
|---|---|
| 단어장 기본 정보 | 제목(필수·60자), 설명(선택·120자), 공개/비공개 라디오, 실시간 글자 수 카운터 |
| 학습 태그 선택 | 수능·토익·토플·텝스·IELTS·일상회화·비즈니스·학술 멀티 선택 |
| 단어 입력 목록 | 동적 항목 추가/삭제/순서변경, 각 항목: 단어(필수)·뜻(필수)·품사·발음기호·예문·메모 |
| 사이드바 | 태그 수·단어 수·완성 단어 수 실시간 집계, 입력 미리보기, 저장 버튼 |

**API 연동:**
```
POST /api/vocab/manual
Authorization: Bearer {accessToken}
Body: { title, description, isPublic, tags[], words[{ word, meaning, pos, ipa, example, tip }] }
→ 성공 시 vocab.html?id={data.id} 이동
```

**`design_only_view.html` 차이점 (원본 대비):**

| 항목 | 원본 | 디자인 전용 |
|---|---|---|
| 인증 체크 | `if (!token) { ... return; }` | 제거 |
| 로그아웃 | `localStorage.removeItem + redirect` | `/* design-preview: no redirect */` |
| 저장 API 호출 | `fetch('/api/vocab/manual', ...)` | 제거, 성공 메시지만 표시 |
| CSS/JS 경로 | `style.css` / `main.js` | `../src/main/resources/static/style.css` 등 |

---

### 버그 수정 이력 — `.reveal` 클래스와 동적 요소 (2026-06-12)

**증상:** 단어 입력 칸이 화면에 전혀 보이지 않음.

**원인:** `style.css`의 `.reveal` 클래스는 `opacity: 0; transform: translateY(24px)`으로 시작하며, `main.js`의 `IntersectionObserver`가 `.visible` 클래스를 추가해야 비로소 보이는 구조. 그런데 `main.js`의 observer는 **페이지 로드 시점에 이미 존재하는 요소만** 등록한다. JS로 나중에 동적 생성된 `.word-entry` 요소들은 observer에 등록되지 않아 영구적으로 `opacity: 0` 상태를 유지했음.

**수정:** `renderEntry()` 함수에서 `el.className = 'word-entry reveal'` → `el.className = 'word-entry'` 로 변경. 동적 생성 요소에는 `.reveal` 클래스를 부여하지 않음.

> **규칙:** JS로 동적 생성하는 DOM 요소에는 `.reveal` 클래스를 사용하지 말 것. `main.js`의 IntersectionObserver는 최초 로드 시 정적으로 존재하는 요소만 감시한다.

---

---

## 단어장 만들기 페이지 통합 (2026-06-12)

### 변경 파일

| 파일 | 변경 내용 |
|---|---|
| `vocab-new.html` | AI 생성 + 직접 입력 두 모드를 한 페이지로 통합 |
| `FrontEnd/vocab-new.html` | 동일 내용의 디자인 미리보기 전용 버전 갱신 |
| `manual-vocab-add.html` | `vocab-new.html`로 기능 흡수 — 단독 페이지로서 역할 종료 |

### 기능 개요

페이지 상단 헤더에 **모드 토글 버튼**을 추가하여 한 페이지에서 두 가지 입력 방식을 전환.

```
✨ AI 자동 생성  |  ✍️ 직접 입력
```

**공통 섹션** (항상 표시):
- 기본 정보 (제목·설명·공개 여부)
- 학습 태그 선택

**AI 모드 전용 섹션** (직접 입력 시 `display:none`):
- 단어 텍스트 입력 (textarea, 줄 단위)
- AI 모델 선택 (Anthropic · OpenAI · Google)
- 사이드바: AI 생성 버튼 + 단어 미리보기

**직접 입력 모드 전용 섹션** (AI 모드 시 `display:none`):
- 단어 항목 동적 추가/삭제/순서변경 (단어·뜻·품사·발음기호·예문·메모)
- 사이드바: 저장 버튼 + 완성 단어 수 + 미리보기

### POST 요청 분리

| 모드 | 엔드포인트 | 주요 바디 |
|---|---|---|
| AI 생성 | `POST /api/vocab/generate` | `{ title, description, isPublic, tags[], words[], aiProvider, apiKey }` |
| 직접 입력 | `POST /api/vocab` | `{ title, tag (문자열, 콤마 join), isPublic, words[{ word, meaning, pos, ipa, examples, memoryTip }] }` |

> **주의:** 직접 입력 모드의 `tag`는 배열이 아닌 **단일 문자열** (`[...selectedTags].join(',')`) — 백엔드 `VocaCreateRequestDTO.tag (String, @NotBlank)` 스펙에 맞춤. 태그 미선택 시 프론트에서 오류 처리.

> **주의:** 직접 입력 모드 단어 필드명 — `examples` (백엔드 `VocaWordRequestDTO.examples`), `memoryTip` (백엔드 `VocaWordRequestDTO.memoryTip`). HTML 입력 필드 `data-field`는 내부적으로 `example` / `memo`를 쓰고, payload 생성 시 변환.

### 성공 후 이동

| 모드 | 이동 대상 |
|---|---|
| AI 생성 | `vocab.html?id={data.id}` |
| 직접 입력 | `dashboard.html` (백엔드 응답에 id 없음) |

---

## index.html 로그인 상태 네브 전환 (2026-06-12)

### 변경 내용

로그인 여부(`localStorage.accessToken` 존재 확인)에 따라 네브바 버튼을 동적으로 교체.

| 상태 | 데스크탑 네브 | 모바일 메뉴 | Hero CTA |
|---|---|---|---|
| 비로그인 | 로그인 + 시작하기 | 로그인 + 시작하기 | 무료로 시작하기 (`#/register`) |
| 로그인 | 대시보드 + 로그아웃 | 대시보드 + 로그아웃 | 대시보드로 이동 (`dashboard.html`) |

**구현 방식:**
- 비로그인 버튼과 로그인 버튼을 HTML에 모두 선언, 기본값은 로그인 버튼 그룹만 표시
- JS에서 토큰 확인 후 `display:none` / `display:''` 토글
- 로그아웃 클릭 시 `accessToken` · `refreshToken` 삭제 후 `location.reload()`

---

### CSS 레이어 계층 규칙 (다크모드 입력 필드 가시성)

다크모드 배경 변수 밝기 순서: `--bg` < `--bg-2` < `--bg-3` < `--bg-4` (bg-4가 가장 밝음)

입력 필드가 보이려면 **컨테이너보다 밝은(높은 번호) 배경**을 써야 한다:

```
.fc (form card)   → background: var(--bg-3)   ← 카드
.word-entry       → background: var(--bg-3)   ← 단어 항목 (테두리로 구분)
.entry-input      → background: var(--bg-4)   ← 입력칸 (bg-3보다 밝아서 보임)
```

**잘못된 패턴 (입력칸 안 보임):**
```
.word-entry  → bg-4   (밝음)
.entry-input → bg-3   (어두움) ← 컨테이너보다 어두워 보이지 않음
```

---

## 커뮤니티 페이지 API 연동 (`community.html`, 2026-06-15)

### 엔드포인트 변경

| 항목 | 변경 전 | 변경 후 |
|---|---|---|
| 호출 URL | `GET /api/community/vocabs?tag=...&sort=...` (미존재) | `GET /api/vocab` |
| 필터·정렬 | 서버 파라미터 | 클라이언트 사이드 처리 |
| 페이지네이션 | 서버 페이지 요청 | `rawData` 슬라이싱 |

### 데이터 흐름

```
loadVocabs()
  → showSkeleton() (6개 shimmer 카드 표시)
  → GET /api/vocab (Authorization 헤더 포함)
  → 응답 배열을 normalize()로 변환 → rawData 저장
  → renderFiltered() 호출

renderFiltered()
  → applyFilter(rawData) : 태그·검색어 필터 + 정렬
  → page 기준 슬라이싱 → allData
  → renderGrid() + "더 보기" 버튼 표시 여부 결정
```

### normalize() — VocaResponseDTO → 내부 포맷

```js
// 서버 응답 필드 → 내부 필드
v.tag       (콤마 문자열)  → tags[]  (배열)
v.nickname                 → author
v.createdAt (LocalDateTime) → updatedAt (YYYY-MM-DD 앞 10자)
```

### 좋아요 / 복사 단순화

- 좋아요: 서버 API 없음 → 클라이언트 상태 토글만 (API 호출 제거)
- 복사: 서버 API 없음 → `vocab.html?id={id}` 로 바로 이동

---

## 대시보드 단어장 목록 API 연동 (`dashboard.html`, 2026-06-15)

### 호출 엔드포인트

`GET /api/vocab/my` — `Authorization: Bearer {token}` 헤더 포함.  
Spring Security가 JWT에서 사용자 정보를 추출해 본인 단어장만 반환.

### normalize() 적용

`VocaResponseDTO` 필드명이 프론트 내부 포맷과 달라 변환 필요:

| 서버 필드 | 내부 필드 | 변환 |
|---|---|---|
| `tag` (콤마 문자열) | `tags[]` | `split(',').map(trim)` |
| `createdAt` (LocalDateTime) | `updatedAt` | `substring(0, 10)` |

### 폴백 변경

API 실패 시 샘플 단어장 표시 → **빈 상태(새 단어장 만들기 안내)**로 변경.  
샘플 데이터가 실제 데이터인 것처럼 오해할 수 있어 제거.

### Scroll reveal 동적 등록

카드가 API 응답 후 동적으로 생성되므로, `renderVocabs()` 내부에서 카드 렌더 직후 IntersectionObserver를 등록.  
(기존 페이지 로드 시점 정적 등록으로는 동적 카드가 `.visible` 미적용되는 문제 방지)

---

## 단어장 세부 페이지 백엔드 연동 (`vocab.html`, 2026-06-15)

### 필드명 불일치 수정

백엔드 `VocaDetailResponseDTO.words`는 `VocaWordRequestDTO` 타입으로, 프론트의 `wordCard()` 함수와 필드명이 달라 수정.

| 백엔드 필드 | 변경 전 (프론트) | 변경 후 (프론트) |
|---|---|---|
| `examples` | `w.example` | `w.examples` |
| `memoryTip` | `w.tip` | `w.memoryTip` |

### 진입 시 단어 미표시 버그 수정

**증상:** 페이지 최초 진입 시 단어 카드가 보이지 않음. 필터 변경 시엔 정상 표시.

**원인:** `wordCard()`가 `.reveal` 클래스를 붙여 카드를 생성하는데, 최초 로드 시 `attachReveal()`을 호출하지 않아 카드가 `opacity: 0` 상태로 유지됨. 필터 변경 이벤트에는 `attachReveal()`이 연결되어 있어 그 시점엔 정상 동작.

**수정:** API 성공 경로와 폴백 경로 양쪽에서 `renderWords()` 직후 `attachReveal()` 호출 추가.

---

## AI 단어 생성 → 단어장 저장 연동 (`vocab-new.html`, 2026-06-22)

### 변경 내용

AI 모드의 **✨ AI로 생성하기** 버튼 클릭 핸들러를 전면 교체.

**변경 전:** 존재하지 않는 `POST /api/vocab/generate` 단일 요청
**변경 후:** 2단계 요청으로 분리

```
1단계: POST /api/ai/generate-word   (AI 단어 분석)
        ↓ WordAnalysisDTO[] 수신
2단계: POST /api/vocab              (단어장 저장)
```

### 필드 변환 로직

`WordAnalysisDTO` → `VocaWordRequestDTO` 변환 시 타입 불일치를 프론트에서 처리.

| AI 응답 필드 | 타입 | 변환 방식 | 저장 필드 |
|---|---|---|---|
| `meanings` | `List<String>` | `.join(', ')` | `meaning` (String) |
| `examples` | `List<{en, ko}>` | `"영문 / 한국어"\n` 형식으로 join | `examples` (String) |
| `word` / `pos` / `ipa` / `memoryTip` | String | 그대로 | 동일 필드명 |

```js
const convertedWords = aiData.map(w => ({
  word:      w.word,
  meaning:   Array.isArray(w.meanings) ? w.meanings.join(', ') : (w.meanings || ''),
  pos:       w.pos       || null,
  ipa:       w.ipa       || null,
  examples:  Array.isArray(w.examples)
               ? w.examples.map(e => `${e.en} / ${e.ko}`).join('\n')
               : (w.examples || null),
  memoryTip: w.memoryTip || null,
}));
```

### 요청 헤더

`/api/ai/generate-word`는 JWT 외에 `X-AI-Api-Key` 헤더가 추가로 필요.

```js
headers: {
  'Content-Type': 'application/json',
  'Authorization': 'Bearer ' + token,
  'X-AI-Api-Key': apiKey,   // localStorage('wc-key-google')에서 읽음
}
```

### 제약 사항

- **현재 Gemini(Google)만 지원.** Google 외 provider 선택 시 오류 배너 표시.
- API Key는 `localStorage('wc-key-google')`에서 읽음. 미설정 시 설정 페이지 안내.
- 저장 성공 시 `dashboard.html`로 이동 (백엔드 `POST /api/vocab` 응답에 id 없음).

### POST 요청 수정 이력

| 항목 | 변경 전 | 변경 후 |
|---|---|---|
| AI 분석 엔드포인트 | `POST /api/vocab/generate` (미존재) | `POST /api/ai/generate-word` |
| 저장 엔드포인트 | (없음, 위에서 통합 처리) | `POST /api/vocab` |
| 성공 후 이동 | `vocab.html?id={data.id}` | `dashboard.html` |

---

## AI 단어 입력 파일 모드 제거 (`vocab-new.html`, 2026-06-24)

### 변경 내용

AI 모드의 단어 입력 방식에서 **📎 파일 업로드 모드를 제거**하고, **✏️ 텍스트 입력만 남김.**

**제거된 요소:**

| 구분 | 제거 항목 |
|---|---|
| CSS | `.input-mode-toggle`, `.input-mode-btn`, `.file-drop-zone*`, `.file-selected*` 스타일 전체 |
| HTML | 서브 토글 버튼 (`✏️ 텍스트 입력` / `📎 파일 업로드`), `#fileInputArea` 드롭존 영역 |
| JS | `aiInputMode` 변수, `switchAiInputMode()` 함수, 파일 드롭존 이벤트 핸들러 일체, generate 핸들러의 파일 분기 (`/api/ai/analyze-file` 호출 포함) |

**변경 후 AI 모드 단어 입력 흐름:**

```
textarea (#wordInput) 에 단어 줄 단위 입력
    ↓
✨ AI로 생성하기 클릭
    ↓
POST /api/ai/generate-word  (JSON, X-AI-Api-Key 헤더 포함)
    ↓
POST /api/vocab  (단어장 저장)
    ↓
dashboard.html 이동
```

> **배경:** vocab-import에 이미 가져오기 기능이 구현되어 있어 불필요.

---

## 커뮤니티 공개 단어장 조회 엔드포인트 이전 (`community.html`, 2026-06-25)

### 배경

공개 단어장 목록 조회 및 복사 로직이 `VocaController` / `VocaService`에서 `CommunityController` / `CommunityService`로 이전됨.

### 엔드포인트 변경

| 기능 | 변경 전 | 변경 후 |
|---|---|---|
| 공개 단어장 목록 조회 | `GET /api/vocab` | `GET /api/community` |
| 단어장 복사 | `POST /api/community/vocabs/{id}/copy` | `POST /api/community/{id}/copy` |
| 좋아요 토글 | `POST/DELETE /api/community/vocabs/{id}/like` | `POST/DELETE /api/community/{id}/like` |

### 복사 버튼 동작 변경 (`static/community.html`)

기존 `copyVocab()`은 API 호출 없이 바로 `vocab.html?id=...`으로 이동하는 더미 구현이었음.
실제 `POST /api/community/{id}/copy` 를 호출하는 비동기 함수로 교체.

```
복사하기 클릭
    ↓
POST /api/community/{id}/copy  (Authorization 헤더 포함)
    ↓
성공(2xx)  → "✅ 복사됨" 표시 후 dashboard.html 이동
실패       → 버튼 원복
```

---

## 커뮤니티 단어장 세부 페이지 신규 생성 (`community-vocab.html`, 2026-06-25)

### 신규 파일

| 파일 | 위치 | 설명 |
|---|---|---|
| `community-vocab.html` | `src/main/resources/static/` | 커뮤니티 공개 단어장 세부 조회 (인증 포함) |

### 기능 개요

커뮤니티 목록에서 단어장 카드를 클릭했을 때 진입하는 세부 조회 페이지.
`vocab.html`과 달리 **읽기 전용**이며 수정·삭제·테스트·북마크 기능 없음.

### 엔드포인트

| 기능 | 엔드포인트 |
|---|---|
| 단어장 세부 조회 | `GET /api/community/{id}` |
| 내 단어장으로 복사 | `POST /api/community/{id}/copy` |

### vocab.html 대비 제거된 요소

| 카테고리 | 제거 항목 |
|---|---|
| 헤더 버튼 | 테스트 시작, 수정, 삭제 버튼 |
| 학습 기능 | 북마크(학습 완료 토글), 학습 진도 바 |
| 필터 | 학습 완료/미학습 필터 (단어 검색만 유지) |
| 수정 모드 | `enterEditMode()`, `renderEditHeader()`, `renderEditWords()`, `saveEdit()` 전체 |
| 삭제 모달 | `#deleteModal` 및 관련 이벤트 |

### 추가된 요소

- "📋 내 단어장으로 복사" 버튼 → `POST /api/community/{id}/copy` 호출 후 `dashboard.html` 이동
- "← 커뮤니티로 돌아가기" 링크 (헤더 상단)

### community.html 카드 클릭 연결

`pubCard()` 함수에서 제목·설명·메타 영역을 `.pub-card__clickable` 래퍼로 감쌈.
클릭 시 `community-vocab.html?id={id}` 로 이동. 하단 "복사하기" 버튼은 기존 동작 유지.

```
카드 본문 클릭  → community-vocab.html?id={id}  (세부 조회)
복사하기 버튼  → POST /api/community/{id}/copy  (바로 복사)
```

---

## 로그아웃 / Access Token 자동 갱신 (`main.js`, 2026-06-27)

### 변경 내용

`main.js` 최상단에 두 개의 전역 함수 추가. 모든 보호 페이지가 이미 `main.js`를 로드하므로 별도 파일 없이 전역에서 사용 가능.

### `authFetch(url, options)` — 인증 fetch 래퍼

기존의 `fetch()` + 수동 `Authorization` 헤더 패턴을 대체.

```
authFetch(url, options) 호출
    ↓
accessToken을 localStorage에서 읽어 Authorization 헤더에 자동 추가
    ↓
응답이 401이면
    ├─ refreshToken 없음 → doLogout()
    └─ refreshToken 있음 → POST /api/auth/refresh 호출
            ├─ 성공 → 새 accessToken 저장 후 원래 요청 재시도
            └─ 실패 → doLogout()
```

**사용법:** 기존 `fetch('/api/...', { headers: { Authorization: 'Bearer ' + token } })` → `authFetch('/api/...')` 로 교체.  
`Content-Type` 등 다른 헤더는 기존과 동일하게 `options.headers`에 전달하며, `Authorization`만 자동 처리된다.

### `doLogout()` — 서버 연동 로그아웃

기존 각 페이지에 중복 정의되어 있던 로컬 `logout()` 함수를 대체.

```
doLogout() 호출
    ↓
POST /api/auth/logout  (서버 DB의 refreshToken → null)
    ↓ (네트워크 오류여도 계속 진행)
localStorage에서 accessToken · refreshToken 삭제
    ↓
login.html 이동
```

> **주의:** `index.html`은 보호 페이지가 아니므로 나중에 네브 로그아웃을 구현할 때 `doLogout()` 대신 `localStorage.removeItem` + `location.reload()` 를 써야 한다.

### 적용 파일

| 파일 | 변경 내용 |
|---|---|
| `main.js` | `authFetch`, `doLogout` 전역 함수 추가 |
| `dashboard.html` | 로컬 `logout()` → `doLogout()`, `fetch()` → `authFetch()` |
| `community.html` | 동일 |
| `community-vocab.html` | 동일 |
| `settings.html` | 동일. 계정 탈퇴 성공 후 `logout()` → `doLogout()` 포함 |
| `vocab.html` | 동일 |
| `test.html` | 동일 |
| `test-result.html` | 동일 |
| `vocab-new.html` | 동일. `X-AI-Api-Key` 헤더는 `options.headers`로 그대로 전달 |
| `vocab-import.html` | 동일. FormData 파일 업로드 시 `Content-Type` 헤더 생략 (브라우저 자동 설정 유지) |
| `manual-vocab-add.html` | 동일 |

### `POST /api/auth/refresh` 엔드포인트

| 항목 | 내용 |
|---|---|
| 요청 바디 | `{ "refreshToken": "..." }` |
| 응답 바디 | `{ "accessToken": "새 토큰" }` |
| 허용 여부 | `SecurityConfig`에서 `permitAll()` 등록됨 (토큰 없이 접근 가능) |

---

## 커뮤니티 좋아요 API 연동 (`community.html`, 2026-06-26)

### 변경 내용

| 항목 | 변경 전 | 변경 후 |
|---|---|---|
| 좋아요 버튼 클릭 | 클라이언트 상태 토글만 (API 호출 없음) | `POST /api/community/{id}/like` 실제 호출 |
| 카드 클릭 시 세부 페이지 이동 | 좋아요 버튼 클릭 시에도 발생 (버블링 문제) | `e.stopPropagation()` 추가로 해결 |
| 좋아요 수 표시 | `v.likes` 필드 참조 → 항상 0 | `v.likeCount` 필드 참조로 수정 (`normalize()` 내부) |

### 좋아요 토글 흐름

```
♥ 버튼 클릭
    ↓
e.stopPropagation()  (카드 클릭 이벤트 차단)
    ↓
POST /api/community/{id}/like  (Authorization 헤더 포함)
    ↓
성공(2xx)  → 카운트 +1/-1, 색상 토글, liked 클래스 토글
실패       → UI 상태 유지
```

**주의:** 좋아요 버튼(`.like-btn`)이 `.pub-card__clickable` 내부에 위치하므로 `e.stopPropagation()` 없이는 카드 세부 페이지로 이동해버림.

---

## 인증 방식 쿠키 통일 + Google OAuth 연동 (2026-06-27)

### 배경

- 기존: JWT를 `localStorage`에 저장 → XSS 취약점
- 변경: JWT를 `HttpOnly` 쿠키에 저장 → JS 접근 불가, XSS 방어
- Google OAuth 로그인도 동일한 쿠키 방식 사용으로 통일

### `main.js` 변경

| 함수 | 변경 전 | 변경 후 |
|---|---|---|
| `authFetch()` | localStorage에서 토큰 읽어 `Authorization` 헤더 추가 | 헤더 제거, `credentials: 'include'` 추가 (쿠키 자동 첨부) |
| `doLogout()` | localStorage 토큰 삭제 후 redirect | localStorage 삭제 제거, `/api/auth/logout` 호출만 유지 |
| `requireAuth()` | 없음 (신규 추가) | `GET /api/auth/me` 호출 → 401이면 `login.html` 이동, 성공 시 `{ email, nickname }` 반환 |

**`authFetch()` 401 처리 흐름 변경:**
```
변경 전: refreshToken을 localStorage에서 읽어 POST /api/auth/refresh { refreshToken } 전송
변경 후: POST /api/auth/refresh credentials: 'include' (쿠키 자동 첨부, 바디 없음)
         → 서버가 refresh_token 쿠키를 읽고 새 access_token 쿠키 발급
```

### `login.html` / `register.html` 변경

| 항목 | 변경 내용 |
|---|---|
| 로그인 성공 처리 | `localStorage.setItem(accessToken/refreshToken)` 제거, 바로 `index.html` 이동 |
| fetch 옵션 | `credentials: 'include'` 추가 |
| GitHub 버튼 | 두 페이지 모두 삭제 |
| Google 버튼 | `/oauth2/authorization/google` 으로 연결 |

### 보호 페이지 인증 체크 변경

**변경 전:**
```js
(function () {
  const token = localStorage.getItem('accessToken');
  if (!token) { window.location.href = 'login.html'; return; }
  ...
})();
```

**변경 후:**
```js
(async function () {
  const user = await requireAuth();   // GET /api/auth/me → 401이면 자동 redirect
  if (!user) return;
  ...
})();
```

**적용 파일:** `dashboard` / `community` / `community-vocab` / `settings` / `vocab` / `test` / `test-result` / `vocab-new` / `vocab-import`

### `index.html` 네브 로그인 상태 변경

```js
// 변경 전
const token = localStorage.getItem('accessToken');
if (token) { /* 네브 전환 */ }
function logout() { localStorage.removeItem(...); location.reload(); }

// 변경 후
fetch('/api/auth/me', { credentials: 'include' }).then(res => {
  if (!res.ok) return;
  /* 네브 전환 */
});
// 로그아웃 → doLogout() 사용
```

### 주의사항

- `wc-theme`, `wc-key-*`, `wc-prefs` 등 **토큰 외 localStorage 항목은 그대로 유지**
- 쿠키 방식 전환으로 **CSRF 취약점 새로 발생** → `CookieUtil.addTokenCookie()`에 `SameSite=Lax` 추가 필요 (배포 전)
- `POST /api/auth/refresh` 요청 바디 없음 — refresh_token은 쿠키로 자동 전달됨

---

## 비밀번호 찾기 페이지 신규 생성 (`forgot-password.html`, 2026-06-29)

### 신규 파일

| 파일 | 위치 | 설명 |
|---|---|---|
| `forgot-password.html` | `src/main/resources/static/` | 비밀번호 찾기 전용 페이지 (인증 불필요) |

### 기능 개요

로그인 페이지의 "비밀번호 찾기" 링크 클릭 시 진입하는 3단계 플로우 페이지.

### 3단계 플로우

| 단계 | UI | 엔드포인트 | 요청 바디 |
|---|---|---|---|
| 1단계 | 이메일 입력 | `POST /api/auth/email/forgotPassword` | `{ email }` |
| 2단계 | 인증 코드 입력 (3분 타이머 + 재발송) | `POST /api/auth/email/verify` | `{ email, code }` |
| 3단계 | 새 비밀번호 설정 (강도 표시) | `POST /api/auth/update-password` | `{ email, newPassword }` |

### 주요 UI 요소

- **단계 표시기:** `.step-dot` (active/done 상태), `.step-line` (done 상태) — `goStep(n)` 함수로 전환
- **3분 카운트다운 타이머:** `startTimer(180)` — 30초 이하 시 빨간색(`urgent`), 만료 시 재발송 버튼 활성화
- **비밀번호 강도 바:** 5단계 (매우 약함 → 매우 강함), 색상 + 레이블 실시간 표시
- **재발송 버튼:** 타이머 만료 전까지 `disabled`, 재발송 성공 시 타이머 리셋

### 보안 처리

이메일 존재 여부와 무관하게 항상 동일한 응답을 반환하도록 백엔드 설계됨 (이메일 열거 공격 방지).  
프론트에서는 1단계 성공 시 항상 2단계로 이동하며 에러를 구분하지 않음.

### login.html 변경

비밀번호 찾기 링크: `href="#forgot"` → `href="forgot-password.html"`

### main.js 변경

`#/forgot-password` 라우트 추가:
```js
'#/forgot-password': 'forgot-password.html',
```

### API 엔드포인트 확정

| 단계 | 메서드 | 엔드포인트 | 요청 바디 |
|---|---|---|---|
| 1단계 (코드 발송) | `POST` | `/api/auth/email/forgotPassword` | `{ email }` |
| 1단계 (재발송) | `POST` | `/api/auth/email/forgotPassword` | `{ email }` |
| 2단계 (코드 인증) | `POST` | `/api/auth/email/verify/forgotPassword` | `{ email, code }` |
| 3단계 (비밀번호 변경) | `PUT` | `/api/auth/reset-password` | `{ newPassword }` |

### 인증 흐름

```
2단계 인증 성공
    ↓
서버가 5분짜리 access_token을 쿠키로 발급
    ↓
3단계 PUT /api/auth/reset-password 요청 시 쿠키 자동 첨부 (credentials: 'include')
    ↓
서버가 JWT에서 이메일 추출 후 비밀번호 변경
```

### 주의사항

- 2단계 응답은 `Boolean` 타입 — `data === true` 로만 판정 (`|| res.ok` 사용 금지)
- 3단계 바디에 이메일 불필요 — JWT 쿠키에서 서버가 추출
- `/api/auth/reset-password`는 `permitAll` 제외 대상 (토큰 인증 필요)

---

## 다중 품사/뜻/예문 구조 적용 (`vocab-new.html`, `vocab.html`, 2026-06-28)

### 배경

백엔드 `VocaWords` 엔티티에서 `pos`, `meanings`, `examples` 필드가 제거되고, 별도의 `VocaWordDetail` 엔티티(1:N)로 분리됨. 한 단어가 여러 품사/뜻/예문 조합을 가질 수 있는 구조로 변경.

### `vocab-new.html` — 수동 입력 모드 변경

**단어 항목(entry) 데이터 구조 변경:**

| 변경 전 | 변경 후 |
|---|---|
| `{ id, word, meaning, pos, ipa, example, memo }` | `{ id, word, ipa, memoryTip, details: [{id, pos, meaning, example}] }` |

**UI 변경:**
- 뜻/품사/예문이 하나의 `detail-row` 그룹으로 묶임
- 품사 선택: `<select>` 드롭다운 → 태그 칩 클릭 방식 (단일 선택)
- `+ 뜻 / 예문 추가` 버튼으로 같은 단어에 여러 그룹 추가 가능
- 그룹이 2개 이상일 때만 `✕ 삭제` 버튼으로 제거 가능 (최소 1개 유지)

**신규 함수:**
- `createDetail()` — detail 객체 생성
- `renderDetailRow(entryObj, detail)` — detail 행 DOM 생성 및 이벤트 연결

**저장 payload 변경:**

```js
// 변경 전
words: [{ word, meaning, pos, ipa, examples, memoryTip }]

// 변경 후
words: [{ word, ipa, memoryTip, vocaWordDetailDTOS: [{ pos, meaning, examples }] }]
```

**`isReady()` 변경:**
```js
// 변경 전
obj.word.trim() && obj.meaning.trim()
// 변경 후
obj.word.trim() && obj.details.some(d => d.meaning.trim())
```

---

### `vocab.html` — 단어 카드 조회 변경

**`wordCard()` 변경:**
- `w.vocaWordDetailDTOS` 배열이 있으면 각 detail을 구분선으로 나눠 카드 내 표시
- 각 detail 칸: 품사 뱃지(`.wc__detail-pos`) + 뜻(`.wc__detail-meaning`) + 예문(`.wc__detail-example`)
- `vocaWordDetailDTOS`가 없는 경우 기존 `w.meaning` / `w.examples` 필드로 폴백

**`filterWords()` 변경:**
- 검색어 매칭 시 `w.meaning` 대신 `vocaWordDetailDTOS` 배열 전체를 순회

---

### `vocab.html` — 단어 수정 모드 변경

**`enterEditMode()` 변경:**
```js
// 변경 전
editWords = wordsData.map(w => ({ ...w }));
// 변경 후
editWords = wordsData.map(w => ({ ...w, details: (w.vocaWordDetailDTOS || []).map(d => ({ ...d })) }));
```

**`buildEditEntry()` 변경:**
- 뜻/품사/예문 단일 입력 필드 제거
- 조회 화면과 동일한 detail 행 구조 (`edit-detail-row`) 적용
- `+ 뜻 / 예문 추가` 버튼으로 detail 행 추가 가능
- 신규 함수 `renderEditDetailRow(wordObj, detail)` 추가

**저장 payload 변경:**
```js
// 변경 전
words: [{ word, ipa, pos, meanings, examples, memoryTip, learned }]
// 변경 후
words: [{ word, ipa, memoryTip, learned, vocaWordDetailDTOS: [{ pos, meaning, examples }] }]
```

**저장 후 즉시 반영 버그 수정:**
```js
// 변경 전 — wordCard()가 읽는 vocaWordDetailDTOS 없음
wordsData = validWords;
// 변경 후
wordsData = validWords.map(w => ({ ...w, vocaWordDetailDTOS: w.details || [] }));
```
새로고침 없이도 수정 결과가 카드에 즉시 반영됨.

---

## 단어장 PDF 출력 · 순서 섞기 · 하단 저장 버튼 (`vocab.html`, `vocab-new.html`, 2026-06-29)

### 순서 섞기 (`vocab.html`)

단어 목록 컨트롤 영역에 **🔀 순서 섞기** 버튼 추가.  
클릭 시 Fisher-Yates 알고리즘으로 `wordsData` 배열을 인플레이스 셔플 후 `renderWords()` 재호출.  
현재 검색·학습 필터 상태는 유지된다 (`filterWords()` 경유).

### PDF 변환 (`vocab.html`)

단어 목록 컨트롤 영역에 **📄 PDF 변환** 버튼 추가. 클릭 시 옵션 모달 오픈.

#### PDF 옵션 모달 구성

| 섹션 | 요소 | 설명 |
|---|---|---|
| 출력 옵션 | 전체 가리기 (마스터 체크박스) | 하위 5개 가리기 체크박스를 일괄 토글 |
| | 단어 가리기 | 단어 텍스트를 빈칸(밑줄)으로 대체 |
| | 발음기호 가리기 | IPA 표기 숨김 |
| | 뜻 가리기 | 뜻 텍스트를 빈칸(밑줄)으로 대체 |
| | 예문 가리기 | 예문 영역 숨김 |
| | 기억 팁 가리기 | memoryTip 영역 숨김 |
| 품사 필터 | 체크박스 (동적) | 현재 단어장의 `vocaWordDetailDTOS[].pos`를 순회해 품사 목록 동적 생성. 미선택 시 전체 출력 |
| 학습 여부 | 라디오 (3종) | 전체 / 학습 완료만 / 미학습만 |

#### 전체 가리기 연동 규칙

- 마스터 체크 → `.pdf-hide-cb` 전체 체크/해제
- 개별 체크박스 변경 → 5개 모두 체크 시 마스터 자동 체크, 하나라도 해제 시 마스터 해제

#### PDF 출력 방식

```
executePrint() 호출
    ↓
옵션에 따라 단어 필터링 (학습 여부 + 품사)
    ↓
인라인 스타일 카드 HTML 생성 (가리기 옵션 적용)
    ↓
<div id="pdfPrintArea"> 를 body에 appendChild
    ↓
window.print()  →  브라우저 인쇄 대화상자 (PDF로 저장)
    ↓
afterprint 이벤트에서 #pdfPrintArea 자동 제거
```

**@media print 규칙:** `body > *:not(#pdfPrintArea) { display: none }` — `#pdfPrintArea` 외 모든 요소를 인쇄 레이아웃에서 제거해 빈 첫 페이지 방지.

PDF 헤더에는 단어장 제목, 총 단어 수, 적용된 옵션 요약(`단어 가림 · 뜻 가림` 등)이 표시됨.

### 하단 저장 버튼

#### `vocab.html` — 수정 모드

`renderEditWords()` 내에서 `+ 단어 추가` 버튼 바로 아래에 `💾 저장` 버튼 동적 생성.  
클릭 시 헤더의 `saveEdit` 함수를 직접 호출.

#### `vocab-new.html` — 수동 입력 모드

`#manualWordSection` 카드 내 `+ 단어 추가` 버튼 아래에 `💾 단어장 저장` 버튼 정적 추가 (`id="saveBtnBottom"`).  
클릭 시 사이드바의 기존 `#saveBtn`을 `.click()` 트리거 — 저장 로직 중복 없이 재사용.

---

## 오답 단어로 단어장 만들기 (`test-result.html`, `test.html`, 2026-06-30)

### 기능 개요

테스트 결과 화면에서 오답 단어만 모아 새 단어장을 생성하는 버튼 추가.

### 버튼 표시 조건

오답(`wrongWords`)이 1개 이상일 때만 액션 버튼 영역에 **📝 틀린 단어로 단어장 만들기** 버튼 표시 (빨간 테두리).

### 데이터 흐름

```
테스트 완료 (test.html finishTest())
    ↓
wrongMap에 memoryTip, details(품사·뜻·예문 배열) 포함해 구성
    ↓
sessionStorage('testResults').wrongWords에 저장:
  { word, ipa, meaning, wrongCount, memoryTip, details }
    ↓
test-result.html 진입
    ↓
📝 버튼 클릭 → createWrongWordVocab()
    ↓
POST /api/vocab
  Body: { title: "오답 단어장 (월 일)", tag: "복습", isPublic: false,
          words: [{ word, ipa, memoryTip, vocaWordDetailDTOS: details }] }
    ↓
성공 → dashboard.html 이동
```

### 핵심 변환 로직 (`test-result.html`)

```js
vocaWordDetailDTOS: (w.details && w.details.length > 0)
  ? w.details.map(d => ({ pos: d.pos || null, meaning: d.meanings || d.meaning || '', examples: d.examples || null }))
  : [{ pos: null, meaning: w.meaning || '', examples: null }]
```

- `details`가 있으면 원본 단어의 모든 품사/뜻/예문 그룹 그대로 전달
- `details`가 없는 폴백일 때만 `meaning` 텍스트로 단순 생성
- `d.meanings` (test.html buildDetails에서 사용하는 필드명) 와 `d.meaning` 양쪽 호환

### 변경 파일

| 파일 | 변경 내용 |
|---|---|
| `test.html` | `wrongMap` 구성 시 `memoryTip` 추가, sessionStorage `wrongWords`에 `details` · `memoryTip` 포함 |
| `test-result.html` | `createWrongWordVocab()` 함수 신규 추가, 버튼 조건부 렌더링, details 기반 payload 생성 |

---

## 대시보드 태그 필터 (`dashboard.html`, 2026-06-30)

### 기능 개요

단어장 그리드 위에 태그 필터 칩을 자동 생성. 태그 클릭 시 해당 태그를 포함한 단어장만 표시.

### 동작 방식

```
loadDashboard() → renderVocabs(raw)
    ↓
buildTagFilter(allVocabs)
  → 전체 단어장의 tags를 Set으로 수집
  → "전체" 칩 + 태그별 칩 동적 생성
    ↓
태그 칩 클릭
  → activeTag 갱신
  → allVocabs.filter(v => v.tags.includes(activeTag)) → renderGrid()
```

- 단어장에 태그가 없으면(tagSet.size === 0) 필터 바 미표시
- `allVocabs` 변수에 전체 목록 유지 → 필터 토글 시 원본 데이터 재사용

### 추가 CSS 클래스

```css
.tag-filter        /* flex, gap:8px, flex-wrap */
.tag-filter__btn   /* 기본 상태 — var(--border) 테두리 */
.tag-filter__btn.active  /* 활성 상태 — var(--accent) 테두리 + accent-dim 배경 */
```

---

## 가져오기(import) 기능 비활성화 (`dashboard.html`, `main.js`, 2026-06-30)

### 변경 내용

| 파일 | 변경 내용 |
|---|---|
| `dashboard.html` | `📥 가져오기` 버튼 삭제 |
| `main.js` | `'#/vocab/import': 'vocab-import.html'` 라우트 주석 처리 |

`vocab-import.html` 파일 자체는 보존 — 필요 시 주석 해제로 복구 가능.

---

## 커뮤니티 단어장 세부 조회 `vocaWordDetailDTOS` 대응 (`community-vocab.html`, 2026-06-30)

### 배경

백엔드가 2026-06-28에 단어 구조를 flat(`meaning`, `pos`, `examples`) → `vocaWordDetailDTOS` 배열로 변경했으나, `community-vocab.html`의 `wordCard()` 함수가 업데이트되지 않아 뜻·품사·예문이 `undefined`로 출력되던 버그.

### 수정 내용

**`wordCard()` 전면 교체:**

```js
// 변경 전
<div class="wc__meaning">${escHtml(w.meaning)}</div>   // undefined
${w.examples ? ...}                                      // undefined

// 변경 후
const details = w.vocaWordDetailDTOS || [];
details.length > 0
  ? details.map(d => pos뱃지 + meaning + example 렌더링)
  : 폴백으로 w.meaning / w.examples 참조
```

**검색 필터 수정:**
```js
// 변경 전
w.word.includes(q) || (w.meaning || '').includes(q)

// 변경 후
w.word.includes(q) || w.vocaWordDetailDTOS.some(d => d.meaning.includes(q))
```

**CSS 추가:** `vocab.html`과 동일한 `.wc__top`, `.wc__details`, `.wc__detail`, `.wc__detail-pos`, `.wc__detail-meaning`, `.wc__detail-example` 스타일 클래스 추가.

> **주의:** 향후 단어 데이터 구조가 변경될 경우 `vocab.html`, `community-vocab.html` 양쪽 모두 업데이트 필요.

---

## AWS EC2 배포 및 HTTPS 설정 (2026-06-30)

### 인프라 구성

| 항목 | 값 |
|---|---|
| 서버 | AWS EC2 (Ubuntu 24.04, t3.micro) |
| 도메인 | `wordcraft-ai.duckdns.org` → `54.66.253.129` (DuckDNS 무료 도메인) |
| DB | AWS RDS MySQL 8.4.8 (프라이빗 서브넷, EC2 경유 접속) |
| 캐시 | Redis (EC2 localhost:6379, 이메일 인증 코드 저장) |
| 리버스 프록시 | Nginx (포트 80 → HTTPS 리다이렉트, 443 → localhost:8080) |
| HTTPS | Let's Encrypt (certbot --nginx) |
| Spring 프로필 | `prod` (`-Dspring.profiles.active=prod`) |

### 배포 절차

```
로컬: ./gradlew clean build -x test
로컬: scp -i C:\study\wordcraft\AWS\wordcraft-key.pem build\libs\wordcraft-0.0.1-SNAPSHOT.jar ubuntu@54.66.253.129:~/
EC2:  pkill -f wordcraft
EC2:  nohup java -Dspring.profiles.active=prod -jar wordcraft-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

### 주요 수정 사항 (배포 준비 중 발견)

| 파일 | 수정 내용 |
|---|---|
| `OAuth2SuccessHandler.java` | `redirect("http://localhost:8080/index.html")` → `redirect("/index.html")` |
| `CookieUtil.java` | `isSecureEnvironment()` — prod 프로필일 때 Secure 플래그 추가 (HTTPS 적용 후 복원) |
| `SecurityConfig.java` | `permitAll()` 에 `/` 경로 추가 (루트 접근 시 401 발생) |
| `application-prod.properties` | OAuth redirect-uri `http://` → `https://` 변경 |

---

## Clean URL 적용 (2026-06-30)

### 개요

모든 페이지 링크에서 `.html` 확장자를 제거하고 `/login`, `/dashboard` 형태의 깔끔한 URL로 변경.

### Nginx rewrite 규칙 (EC2 `/etc/nginx/sites-available/wordcraft`)

```nginx
location = /login          { rewrite ^ /login.html break; proxy_pass http://localhost:8080; }
location = /register       { rewrite ^ /register.html break; proxy_pass http://localhost:8080; }
location = /dashboard      { rewrite ^ /dashboard.html break; proxy_pass http://localhost:8080; }
location = /vocab-new      { rewrite ^ /vocab-new.html break; proxy_pass http://localhost:8080; }
location = /vocab          { rewrite ^ /vocab.html break; proxy_pass http://localhost:8080; }
location = /community      { rewrite ^ /community.html break; proxy_pass http://localhost:8080; }
location = /community-vocab { rewrite ^ /community-vocab.html break; proxy_pass http://localhost:8080; }
location = /settings       { rewrite ^ /settings.html break; proxy_pass http://localhost:8080; }
location = /test           { rewrite ^ /test.html break; proxy_pass http://localhost:8080; }
location = /test-result    { rewrite ^ /test-result.html break; proxy_pass http://localhost:8080; }
location = /forgot-password { rewrite ^ /forgot-password.html break; proxy_pass http://localhost:8080; }
location = /gemini-guide   { rewrite ^ /gemini-guide.html break; proxy_pass http://localhost:8080; }
location = /policy         { rewrite ^ /policy.html break; proxy_pass http://localhost:8080; }
```

> **주의:** `last` 대신 `break` + `proxy_pass` 를 써야 URL 바에 `.html`이 노출되지 않는다. `last`는 Nginx 내부 재처리라 Spring Boot가 다시 `.html`로 redirect하는 문제 발생.

### 변경된 링크 패턴

| 변경 전 | 변경 후 |
|---|---|
| `href="login.html"` | `href="/login"` |
| `href="dashboard.html"` | `href="/dashboard"` |
| `window.location.href = 'dashboard.html'` | `window.location.href = '/dashboard'` |
| `href="index.html"` | `href="/"` |

**적용 파일:** 모든 `.html` 정적 파일 + `main.js`

---

## index.html 핵심 기능 섹션 주석 처리 (2026-06-30)

`<!-- FEATURES -->` 섹션("핵심 기능 / WordCraft AI가 특별한 이유") 전체를 HTML 주석으로 처리.  
복원하려면 `index.html`에서 해당 `<!-- ... -->` 주석을 제거하면 된다.

---

## settings.html API Key 상태 표시 버그 수정 (2026-06-30)

### 원인

`KEY_MAP = { anthropic: ..., openai: ..., google: ... }` 에서 anthropic·openai 항목의 `id="status-anthropic"`, `id="status-openai"` DOM 요소가 HTML에 없어 `loadKeys()` 가 첫 번째 provider에서 TypeError 발생 → google 상태 업데이트까지 도달하지 못함.

### 수정

```js
// 변경 전
const KEY_MAP = { anthropic: 'wc-key-anthropic', openai: 'wc-key-openai', google: 'wc-key-google' };

// 변경 후
const KEY_MAP = { google: 'wc-key-google' };
```

Anthropic·OpenAI는 아직 미구현이므로 KEY_MAP에서 제외. 향후 구현 시 HTML에 `id="status-anthropic"` 요소 추가 후 KEY_MAP에 재등록.
