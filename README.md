# 🔤 WordCraft AI

> **AI 기반 맞춤형 영단어 학습 플랫폼**  
> 수능·토익·토플 등 원하는 태그를 선택하면 AI가 뜻·예문·품사·기억팁을 자동으로 생성합니다.

🌐 **서비스 주소:** https://wordcraft-ai.duckdns.org

---

## 📌 프로젝트 소개

기존 단어장 서비스는 단어의 일반적인 뜻만 제공합니다.  
**WordCraft AI**는 사용자가 선택한 시험/상황 태그에 맞게 AI가 최적화된 의미와 예문을 자동 생성하고, 단어장을 커뮤니티에서 공유하며 함께 학습할 수 있는 플랫폼입니다.

### 핵심 기능

| | 기능 |
|---|---|
| 🏷️ | **맥락 기반 AI 생성** — 태그별 최적화된 단어 뜻·예문·발음기호·기억팁 자동 생성 |
| ✍️ | **직접 입력** — AI 없이 수동으로 단어·뜻·예문 직접 입력 |
| 🔑 | **내 AI Key 사용** — Gemini API Key를 브라우저에만 저장, 서버 미보관 |
| 🌐 | **커뮤니티 공유** — 단어장을 공개하고 다른 사용자 단어장 복사 |
| 📝 | **다양한 테스트** — 뜻 맞추기·빈칸·플래시카드·스펠링·연결 + 오답 노트 |
| 📄 | **PDF 변환** — 단어장을 가리기 옵션 포함해 PDF로 출력 |
| 🔀 | **순서 섞기** — Fisher-Yates 알고리즘으로 단어 순서 무작위 배치 |

---

## 🛠️ 기술 스택

| 구분 | 기술 |
|---|---|
| **Frontend** | HTML5 / CSS3 / Vanilla JS (ES2022+), SPA 구조 |
| **Backend** | Spring Boot 4.0.6 (Java 21), REST API |
| **Database** | MySQL 8.4 (AWS RDS) |
| **Storage** | AWS S3 (이미지·파일 업로드) |
| **Cache** | Redis (이메일 인증 코드) |
| **Auth** | JWT HttpOnly Cookie + Spring Security + Google OAuth2 |
| **AI** | Google Gemini 2.5 Flash (무료 티어, 사용자 Key 방식) |
| **Server** | AWS EC2 t3.micro (Ubuntu 24.04) |
| **Proxy** | Nginx (HTTPS 443, 클린 URL 라우팅) |
| **HTTPS** | Let's Encrypt (certbot) |
| **Domain** | DuckDNS (`wordcraft-ai.duckdns.org`) |
| **Build** | Gradle |

---

## 📁 프로젝트 구조

```
wordcraft/
├── src/
│   └── main/
│       ├── java/com/example/wordcraft/
│       │   ├── Config/          # SecurityConfig, WebConfig 등
│       │   ├── Controller/      # REST 컨트롤러
│       │   ├── DTO/             # 요청·응답 DTO
│       │   ├── Entity/          # JPA 엔티티
│       │   ├── Exception/       # 예외 처리
│       │   ├── Handler/         # OAuth2 성공 핸들러
│       │   ├── JWT/             # JWT 생성·검증·필터
│       │   ├── Repository/      # Spring Data JPA
│       │   ├── Service/         # 비즈니스 로직
│       │   └── Util/            # CookieUtil 등
│       └── resources/
│           ├── application.properties          # 공통 설정 (메일, S3 등)
│           ├── application-prod.properties     # ⛔ .gitignore — 프로덕션 시크릿
│           ├── application-local.properties    # ⛔ .gitignore — 로컬 개발
│           └── static/                         # Spring Boot 정적 리소스
│               ├── style.css                   # 공통 스타일 (다크/라이트 테마)
│               ├── main.js                     # 공통 JS (authFetch, doLogout, requireAuth)
│               ├── index.html                  # 메인 랜딩 페이지
│               ├── login.html
│               ├── register.html
│               ├── forgot-password.html
│               ├── dashboard.html
│               ├── vocab-new.html              # 단어장 만들기 (AI + 직접 입력)
│               ├── vocab.html                  # 단어장 상세·수정
│               ├── community.html
│               ├── community-vocab.html        # 커뮤니티 단어장 세부 조회
│               ├── test.html
│               ├── test-result.html
│               ├── settings.html
│               ├── gemini-guide.html           # Gemini API Key 발급 가이드
│               └── policy.html
├── FrontEnd/
│   ├── CLAUDE.md                               # 프론트엔드 전체 작업 기록
│   └── DEPLOY.md                               # 배포 및 서버 관리 가이드
└── README.md
```

---

## 🖥️ 페이지 구성

| 페이지 | URL | 설명 |
|---|---|---|
| 메인 | `/` | 랜딩 페이지, 서비스 소개 |
| 로그인 | `/login` | 이메일 로그인 + Google OAuth2 |
| 회원가입 | `/register` | 이메일 회원가입 (이메일 인증) |
| 비밀번호 찾기 | `/forgot-password` | 이메일 인증 3단계 비밀번호 재설정 |
| 대시보드 | `/dashboard` | 내 단어장 목록, 태그 필터 |
| 단어장 만들기 | `/vocab-new` | AI 자동 생성 + 직접 입력 모드 전환 |
| 단어장 상세 | `/vocab?id={id}` | 단어 카드, 검색·필터, 수정, PDF 변환 |
| 커뮤니티 | `/community` | 공개 단어장 탐색·검색·좋아요·복사 |
| 커뮤니티 세부 | `/community-vocab?id={id}` | 공개 단어장 읽기 전용 조회 |
| 테스트 | `/test?vocabId={id}` | 5가지 유형 테스트 |
| 테스트 결과 | `/test-result` | 점수·오답 분석, 오답 단어장 생성 |
| 설정 | `/settings` | 프로필, Gemini API Key, 비밀번호 변경 |
| Gemini 가이드 | `/gemini-guide` | Gemini API Key 발급 5단계 안내 |

---

## 🔐 인증 방식

### JWT HttpOnly Cookie

XSS 공격 방지를 위해 JWT를 `localStorage` 대신 **HttpOnly 쿠키**에 저장합니다.

```
로그인 성공
    ↓
서버: access_token (15분) + refresh_token (7일) 쿠키 발급
    ↓
브라우저: 쿠키 자동 첨부 (JS 접근 불가)
    ↓
access_token 만료 시 → POST /api/auth/refresh 자동 재발급
```

### Google OAuth2

`/oauth2/authorization/google` → 구글 인증 → `OAuth2SuccessHandler` → 쿠키 발급 → `/index.html` 리다이렉트

### 이메일 인증 (회원가입·비밀번호 찾기)

Redis에 인증 코드를 TTL로 저장 → 네이버 SMTP로 발송 → 3분 이내 인증

---

## 🤖 AI 연동 방식

현재 **Google Gemini 2.5 Flash 무료 티어**만 지원합니다.

```
사용자: API Key를 브라우저 localStorage에 저장
    ↓
AI 생성 요청 시 → X-AI-Api-Key 헤더로 서버 전송 (HTTPS 암호화)
    ↓
서버: POST /api/ai/generate-word → Gemini API 중계
    ↓
응답: WordAnalysisDTO[] (word, pos, ipa, meanings, examples, memoryTip)
    ↓
POST /api/vocab 으로 단어장 저장
```

---

## ⚙️ 주요 데이터 모델

| 테이블 | 주요 컬럼 | 설명 |
|---|---|---|
| `users` | id, email, nickname, password, provider, refresh_token | 회원 (provider=google: OAuth 사용자) |
| `vocabularies` | id, user_id, origin_id, title, tag, is_public | 단어장 (origin_id=null: 직접 생성) |
| `voca_words` | id, vocabulary_id, word, ipa, memory_tip, learned | 단어 |
| `voca_word_detail` | id, voca_word_id, pos, meanings, examples | 품사·뜻·예문 (1:N) |

> **originId:** 커뮤니티에서 복사한 단어장은 `originId`에 원본 id 저장. `null`이면 직접 생성한 단어장.

---

## 🚀 로컬 실행 방법

### 사전 요구사항

- Java 21+
- Gradle
- Redis (`localhost:6379`)

### 1. 저장소 클론

```bash
git clone https://github.com/<your-username>/wordcraft.git
cd wordcraft
```

### 2. 로컬 환경 설정 파일 생성

`src/main/resources/application-local.properties` 를 직접 생성합니다.

```properties
spring.datasource.url=jdbc:h2:mem:wordcraft
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

jwt.secret=YOUR_BASE64_SECRET_KEY

spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google
spring.security.oauth2.client.registration.google.scope=email,profile

spring.data.redis.host=localhost
spring.data.redis.port=6379

cloud.aws.credentials.access-key=YOUR_ACCESS_KEY
cloud.aws.credentials.secret-key=YOUR_SECRET_KEY
```

### 3. 빌드 및 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 4. 브라우저 접속

```
http://localhost:8080
```

---

## 📦 프로덕션 배포

자세한 내용은 [`FrontEnd/DEPLOY.md`](FrontEnd/DEPLOY.md) 참고.

```bash
# 1. 빌드
./gradlew clean build -x test

# 2. EC2 전송
scp -i C:\study\wordcraft\AWS\wordcraft-key.pem build\libs\wordcraft-0.0.1-SNAPSHOT.jar ubuntu@54.66.253.129:~/

# 3. EC2 재시작
pkill -f wordcraft
nohup java -Dspring.profiles.active=prod -jar wordcraft-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

---

## 🏷️ 지원 학습 태그

`수능` `토익 (TOEIC)` `토플 (TOEFL)` `텝스 (TEPS)` `IELTS` `일상회화` `비즈니스` `학술` `+ 커스텀 태그`

---

## 🎨 디자인 시스템

- **기본 테마:** 단색 다크 팔레트 (`#0d0d0f` 베이스)
- **포인트 컬러:** 퍼플 계열 (`#6c63ff`)
- **라이트 모드:** 네브바 우상단 🌙/☀️ 버튼, `localStorage('wc-theme')` 저장
- **반응형:** 모바일 햄버거 메뉴 지원

---

## ⚠️ 주의사항

- `application-prod.properties`, `application-local.properties` 는 `.gitignore` 적용 — 절대 커밋 금지
- AI API Key는 브라우저 `localStorage`에만 저장, 서버 DB에 저장되지 않음
- 현재 Gemini 무료 티어만 지원 (분당·일일 요청 수 제한 있음)

---

## 📄 라이선스

This project is for personal/educational use.
