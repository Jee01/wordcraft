# 🔤 WordCraft AI

> **AI 기반 맞춤형 영단어 학습 플랫폼**  
> 수능·토익·토플 등 원하는 태그를 선택하면 AI가 뜻·예문·품사·기억팁을 자동으로 생성합니다.

---

## 📌 프로젝트 소개

기존 단어장 서비스는 단어의 일반적인 뜻만 제공합니다.  
**WordCraft AI**는 사용자가 선택한 시험/상황 태그에 맞게 AI가 최적화된 의미와 예문을 자동 생성하고, 단어장을 커뮤니티에서 공유하며 함께 학습할 수 있는 플랫폼입니다.

### 핵심 가치

| | 기능 |
|---|---|
| 🏷️ | **맥락 기반 학습** — 태그별 최적화된 단어 뜻·예문 자동 생성 |
| 🔑 | **내 AI Key 사용** — OpenAI·Claude·Gemini 등 직접 입력, 서버 미보관 |
| 📂 | **멀티 입력 변환** — 텍스트·이미지·PDF를 단어장으로 자동 변환 |
| 🌐 | **커뮤니티 공유** — 단어장을 공개하고 다른 사용자 단어장 복사 |
| 📝 | **다양한 테스트** — 플래시카드·빈칸채우기·오답노트·결과 통계 |

---

## 🛠️ 기술 스택

| 구분 | 기술 |
|---|---|
| **Frontend** | HTML5 / CSS3 / Vanilla JS (ES2022+), SPA (Hash Router) |
| **Backend** | Spring Boot 3.x (Java 17), REST API |
| **Database** | MySQL (AWS RDS Free Tier) |
| **Storage** | AWS S3 (이미지·파일 업로드) |
| **Auth** | JWT (AccessToken / RefreshToken) + Spring Security |
| **AI API** | OpenAI GPT-4o / Anthropic Claude / Google Gemini (사용자 Key 방식) |
| **Server** | AWS EC2 t2.micro |
| **Build** | Gradle |

---

## 📁 프로젝트 구조

```
wordcraft/
├── src/
│   └── main/
│       ├── java/                          # Spring Boot 백엔드
│       └── resources/
│           ├── application.properties     # 공통 설정
│           ├── application-prod.properties  # ⛔ .gitignore 처리 (시크릿 포함)
│           ├── application-local.properties # ⛔ .gitignore 처리
│           ├── style.css                  # 공통 스타일 (다크/라이트 테마)
│           ├── main.js                    # 공통 스크립트
│           └── html/
│               ├── index.html             # 메인 랜딩 페이지
│               ├── login.html             # 로그인
│               └── register.html          # 회원가입
├── FrontEnd/
│   └── CLAUDE.md                          # 프론트엔드 작업 기록
├── .gitignore
└── README.md
```

---

## 🖥️ 화면 구성

| 화면 | URL (Hash) | 설명 |
|---|---|---|
| 메인 | `/` | 랜딩 페이지, 서비스 소개 |
| 로그인 | `#/login` | 이메일 + 소셜 로그인 |
| 회원가입 | `#/register` | 이메일 + 소셜 가입 |
| 대시보드 | `#/dashboard` | 내 단어장 목록, 최근 학습 현황 |
| 단어장 생성 | `#/vocab/new` | 태그 선택, 단어 입력, AI 생성 |
| 단어장 상세 | `#/vocab/:id` | 단어 목록 조회·수정·삭제 |
| 자동 변환 | `#/vocab/import` | 텍스트·이미지·파일 업로드 |
| 테스트 | `#/test/:vocabId` | 테스트 유형 선택·풀기 |
| 결과 | `#/test/:id/result` | 정답률·오답 분석 |
| 커뮤니티 | `#/community` | 공개 단어장 탐색·검색 |
| 설정 | `#/settings` | 프로필, AI API Key 관리 |

---

## 🚀 로컬 실행 방법

### 사전 요구사항
- Java 17+
- Gradle
- MySQL 또는 H2 (테스트용)

### 1. 저장소 클론

```bash
git clone https://github.com/<your-username>/wordcraft.git
cd wordcraft
```

### 2. 환경 설정 파일 생성

`src/main/resources/application-local.properties` 를 직접 생성합니다.  
(`.gitignore`에 의해 추적되지 않는 파일입니다.)

```properties
# DB (로컬 H2 또는 MySQL)
spring.datasource.url=jdbc:h2:mem:wordcraft
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop

# AWS (로컬 테스트 시 생략 가능)
cloud.aws.credentials.access-key=YOUR_ACCESS_KEY
cloud.aws.credentials.secret-key=YOUR_SECRET_KEY
cloud.aws.s3.bucket=YOUR_BUCKET_NAME
```

### 3. 빌드 및 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 4. 프론트엔드 접속

서버 실행 후 브라우저에서:

```
http://localhost:8080
```

또는 `src/main/resources/html/index.html` 을 브라우저로 직접 열어도 됩니다.

---

## 🔐 AI API Key 처리 방식

보안과 비용 효율을 위해 **서버는 AI API Key를 저장하지 않습니다.**

```
사용자 입력 → 브라우저 LocalStorage 저장
     ↓
AI 요청 시 요청 헤더에 포함 (HTTPS 암호화)
     ↓
서버는 외부 AI API로 중계(Proxy)만 수행
     ↓
응답 후 Key는 서버 메모리에서 즉시 소거 (DB 미저장)
```

### 지원 AI 모델

| 제공사 | 모델 | 특징 |
|---|---|---|
| OpenAI | GPT-4o, GPT-4o mini | 범용적, 한국어 지원 우수 |
| Anthropic | Claude 3.5 Sonnet, Haiku | 긴 문맥 처리, 안전성 |
| Google | Gemini 1.5 Pro, Flash | 무료 할당량 존재 |

---

## 🏷️ 지원 학습 태그

`수능` `토익 (TOEIC)` `토플 (TOEFL)` `텝스 (TEPS)` `IELTS`  
`일상회화` `비즈니스` `학술` `+ 커스텀 태그`

---

## 🎨 디자인 시스템

- **기본 테마:** 단색 다크 팔레트 (`#0d0d0f` 베이스)
- **포인트 컬러:** 퍼플 계열 (`#6c63ff`)
- **라이트 모드:** 네브바 우상단 🌙/☀️ 버튼으로 전환, `localStorage` 저장
- **반응형:** 모바일 햄버거 메뉴 지원

---

## ⚙️ 주요 데이터 모델

| 테이블 | 주요 컬럼 | 설명 |
|---|---|---|
| `users` | id, email, nickname, created_at | 회원 정보 |
| `vocabularies` | id, user_id, title, tag, is_public | 단어장 기본 정보 |
| `vocab_words` | id, vocab_id, word, ipa, pos, meanings, examples, memory_tip | 단어 상세 (JSON 포함) |
| `community_likes` | id, user_id, vocab_id | 좋아요 |
| `community_bookmarks` | id, user_id, vocab_id | 북마크 |
| `comments` | id, user_id, vocab_id, content | 댓글 |
| `test_results` | id, user_id, vocab_id, score, wrong_words, taken_at | 테스트 결과 |

---

## 📋 개발 현황

- [x] 메인 랜딩 페이지 (`index.html`)
- [x] 로그인 페이지 (`login.html`)
- [x] 회원가입 페이지 (`register.html`)
- [x] 다크 / 라이트 모드 토글
- [ ] 대시보드
- [ ] 단어장 생성 (AI 연동)
- [ ] 단어장 상세 / 수정
- [ ] 멀티 입력 자동 변환 (이미지·파일)
- [ ] 테스트 기능
- [ ] 커뮤니티 페이지
- [ ] API Key 설정 페이지
- [ ] Spring Boot REST API 구현
- [ ] JWT 인증

---

## ⚠️ 주의사항

`application-prod.properties` 와 `application-local.properties` 는 `.gitignore` 에 의해 Git 추적에서 제외됩니다.  
절대 해당 파일을 강제로 커밋하거나 공개 저장소에 업로드하지 마세요.

---

## 📄 라이선스

This project is for personal/educational use.
