# 📈 Investory

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)
![React](https://img.shields.io/badge/React-18-61DAFB)
![TypeScript](https://img.shields.io/badge/TypeScript-5.6-blue)

**모의투자와 게이미피케이션을 결합한 투자 학습 플랫폼**

[Features](#-features) • [Tech Stack](#-tech-stack) • [Getting Started](#-getting-started) • [Documentation](#-documentation) • [API](#-api) • [Deployment](#-무료-배포-가이드)

</div>

---

## 📋 Overview

Investory는 실제 주식 데이터를 기반으로 모의투자를 경험하고, 게이미피케이션 요소를 통해 재미있게 투자를 학습할 수 있는 플랫폼입니다.

### 핵심 가치

- 🎯 **안전한 투자 학습**: 실제 돈 없이 실전 같은 투자 경험
- 🎮 **게이미피케이션**: 레벨, 경험치, 업적 시스템으로 동기부여
- 📊 **WMTI 투자 성향 분석**: 나만의 투자 성향을 파악하고 맞춤 전략 수립
- 📈 **시뮬레이터 기반 시세 갱신 + SSE 스트리밍**: 랜덤워크로 생성한 가상 시세를 주기적으로 갱신하고 SSE 로 push (실거래소 연동 아님)
- 💰 **현금 잔액 + 지정가 주문**: 시드머니 기반 매매, 잔액 정합성 보장, 지정가 자동 체결
- 🌏 한국(KOSPI, KOSDAQ) 및 미국(NASDAQ, NYSE) 종목 데이터 지원

---

## ✨ Features

### 🔐 인증/계정
- 회원가입 및 로그인 (JWT 기반, Access/Refresh Token)
- 아이디/이메일/닉네임 중복 확인 (실시간 API 검증)
- 토큰 자동 갱신 (Axios 인터셉터)
- 프로필 관리 및 비밀번호 변경
- 회원 탈퇴

### 📊 주식 거래
- 주식 목록 조회 및 검색 (시장/섹터/키워드 필터)
- 주식 상세 정보 조회 (호가창 포함)
- **시장가 주문**: 접수 즉시 현재가로 체결
- **지정가 주문**: PENDING 대기 후 시세가 조건을 만족하면 자동 체결
- 주문 내역 및 체결 현황 조회, 미체결 주문 취소
- 관심종목 추가/삭제

### 💰 현금 잔액
- 회원가입 시 시드머니 1천만 원 지급
- 매수 시 잔액 검증 및 차감, 매도 시 잔액 증가
- 모든 잔액 변동을 `CashHistory` 에 append-only 감사 로그로 기록
- 동시 주문 상황에서도 잔액이 음수가 되지 않도록 낙관적 락 + 재시도로 보호

### 📡 시세 갱신
- 랜덤워크 기반 시뮬레이터가 주기적으로 전 종목 현재가·변동률·거래량 갱신
- SSE 로 시세 변동과 체결 알림을 클라이언트에 push
- 시드 고정 시 재현 가능한 시세 시퀀스 생성 (테스트/디버깅용)

### 💼 포트폴리오
- 보유 종목 현황 (국내/해외 자동 분류)
- 종목별 손익 분석 및 총 수익률 추적
- 포트폴리오 차트 시각화 (Chart.js 도넛 차트)
- 투자 현황 대시보드

### 🏠 홈 대시보드
- 포트폴리오 요약 (총 자산, 수익률)
- 인기 종목 (Top Stocks API 연동)
- 관심종목 목록 (Watchlist API 연동)
- 시장 지수 현황

### 🧠 WMTI (투자 성향 분석)
- 투자 성향 테스트 문항 (API에서 동적 로딩)
- 테스트 결과 제출 및 유형 분석 (API 연동)
- 기존 결과 조회 및 재검사 지원
- 8가지 투자자 유형 분석 결과 카드

### 🏆 게이미피케이션
- 레벨 시스템 (경험치 기반, API 연동)
- 업적 시스템 (달성률, 잠금 상태 표시)
- 업적별 아이콘 매핑

### 👤 마이페이지
- 프로필 정보 조회 (API 연동)
- 최근 거래 내역 (주문 API 연동)
- 앱 설정 (알림, 다크모드 등)

---

## 🛠 Tech Stack

### Backend
| Technology | Version | Description |
|------------|---------|-------------|
| **Java** | 21 | 프로그래밍 언어 |
| **Spring Boot** | 3.4.1 | 애플리케이션 프레임워크 |
| **Spring Security** | 6.x | 인증 및 인가 |
| **Spring Data JPA** | 3.x | ORM 및 데이터 액세스 |
| **JWT (jjwt)** | 0.12.x | 토큰 기반 인증 |
| **MySQL** | 8.0 | 관계형 데이터베이스 |
| **Redis** | 7.x | 캐싱 및 세션 저장소 |
| **Spring Retry** | 2.x | 낙관적 락 충돌 시 트랜잭션 재시도 |
| **Spring Events** | - | 도메인 간 결합 분리 (`@TransactionalEventListener`) |
| **SSE** | - | 시세·체결 알림 실시간 스트리밍 |
| **JUnit 5 / AssertJ** | - | 단위·통합 테스트 |
| **H2** | 2.x | 테스트용 인메모리 DB (MySQL 호환 모드) |

### Frontend
| Technology | Version | Description |
|------------|---------|-------------|
| **React** | 18.3 | UI 라이브러리 |
| **TypeScript** | 5.6 | 타입 시스템 |
| **Vite** | 6.0 | 빌드 도구 |
| **TailwindCSS** | 3.4 | 유틸리티 CSS 프레임워크 |
| **Zustand** | 5.0 | 클라이언트 상태 관리 (인증) |
| **React Query** | 5.x | 서버 상태 관리 (API 캐싱) |
| **React Router** | 7.x | SPA 라우팅 (Lazy Loading) |
| **Axios** | 1.x | HTTP 클라이언트 (인터셉터) |
| **Chart.js** | 4.x | 차트 시각화 |
| **Framer Motion** | 11.x | 애니메이션 |
| **react-hook-form** | 7.x | 폼 관리 |
| **react-hot-toast** | 2.x | 토스트 알림 |

### DevOps & Infrastructure
| Technology | Description |
|------------|-------------|
| **Docker** | 컨테이너화 |
| **Docker Compose** | 멀티 컨테이너 관리 |
| **Nginx** | 웹 서버 및 리버스 프록시 |

---

## 📁 Project Structure

```
investory/
├── backend/                          # Spring Boot 백엔드
│   └── src/main/java/com/investory/backend/
│       ├── domain/                   # 도메인별 패키지
│       │   ├── auth/                 # 인증 (로그인, 회원가입, 토큰 갱신)
│       │   ├── user/                 # 사용자 + 현금 잔액
│       │   │   ├── entity/           #   User(cash), CashHistory
│       │   │   └── service/          #   CashService, SeedMoneyBackfillRunner
│       │   ├── stock/                # 주식 + 시세 시뮬레이터
│       │   │   ├── event/            #   StockPriceUpdatedEvent
│       │   │   └── service/          #   StockPriceSimulator
│       │   ├── portfolio/            # 포트폴리오 (보유종목, 수익률)
│       │   ├── order/                # 주문 (접수 · 체결 · 매칭)
│       │   │   ├── event/            #   OrderFilledEvent
│       │   │   └── service/          #   OrderService(재시도) → OrderPlacer(트랜잭션)
│       │   │                         #   → OrderExecutor(체결), LimitOrderMatcher
│       │   ├── wmti/                 # WMTI (투자 성향 테스트)
│       │   └── reward/               # 리워드 (레벨, 업적, 경험치 적립 리스너)
│       └── global/                   # 공통 설정
│           ├── common/               # 공통 DTO (ApiResponse 등)
│           ├── config/               # 설정 (Security, Retry, Scheduling)
│           ├── exception/            # 글로벌 예외 처리
│           ├── health/               # 헬스체크
│           ├── security/             # JWT 필터, 인증 설정
│           └── sse/                  # SSE 스트리밍
├── frontend/                         # React 프론트엔드
│   └── src/
│       ├── components/               # 재사용 가능한 UI 컴포넌트
│       │   ├── auth/                 # 인증 관련 (ProtectedRoute)
│       │   ├── common/               # 공통 (Button, Card, Modal 등)
│       │   └── layout/               # 레이아웃 (Header, Footer, Nav)
│       ├── hooks/                    # React Query 커스텀 훅
│       │   └── useApi.ts             # 전체 API 훅 (20+ hooks)
│       ├── pages/                    # 페이지 컴포넌트 (13개 페이지)
│       ├── services/                 # API 서비스 레이어 (8개 서비스)
│       │   ├── api.ts                # Axios 인스턴스 (JWT 인터셉터)
│       │   ├── authService.ts        # 인증 API
│       │   ├── stockService.ts       # 주식 API
│       │   ├── orderService.ts       # 주문 API
│       │   ├── portfolioService.ts   # 포트폴리오 API
│       │   ├── wmtiService.ts        # WMTI API
│       │   ├── rewardService.ts      # 리워드 API
│       │   └── userService.ts        # 사용자 API
│       ├── stores/                   # Zustand 스토어
│       │   └── authStore.ts          # 인증 상태 관리
│       ├── types/                    # TypeScript 타입 정의
│       └── utils/                    # 유틸리티 함수
├── docs/                             # 프로젝트 문서
├── docker/                           # Docker 설정
│   └── mysql/init/01-init.sql        # DB 초기화 스크립트
├── docker-compose.yml                # 프로덕션 Compose
├── docker-compose.dev.yml            # 개발용 Compose
├── Dockerfile                        # 멀티스테이지 빌드
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- **Docker** & **Docker Compose** (권장)
- **Java 21+** (로컬 백엔드 개발 시)
- **Node.js 20+** (로컬 프론트엔드 개발 시)

### Quick Start (Docker)

```bash
# 1. 저장소 클론
git clone https://github.com/your-username/investory.git
cd investory

# 2. Docker Compose로 전체 실행
docker-compose up -d

# 3. 서비스 확인
# Frontend: http://localhost
# Backend API: http://localhost:8080/api
```

### Local Development

#### Backend
```bash
cd backend

# Gradle Wrapper로 실행
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/investory-backend-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
# → http://localhost:5173
```

### Environment Variables

`.env` 파일을 생성하고 다음 환경변수를 설정하세요:

```env
# Database
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=investory
MYSQL_USER=investory
MYSQL_PASSWORD=your_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# CORS (프론트엔드 도메인, 콤마 구분)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173

# 현금 정책
SEED_MONEY=10000000          # 회원가입 시 지급 시드머니
CASH_BACKFILL=true           # 기존 유저에게 소급 지급 (DEPOSIT 이력 기준 멱등)

# 주문 동시성 락 전략: optimistic | pessimistic
ORDER_LOCK_MODE=optimistic

# 시세 시뮬레이터
SIMULATOR_ENABLED=true
SIMULATOR_INTERVAL_MS=5000   # 갱신 주기(ms)
SIMULATOR_VOLATILITY=0.005   # 틱당 최대 변동률 (±0.5%)
```

---

## 📖 Documentation

자세한 문서는 [docs](./docs) 폴더를 참고하세요:

| 문서 | 설명 |
|------|------|
| [기능 명세서](./docs/FEATURE_SPEC.md) | 전체 기능 상세 명세 |
| [API 명세서](./docs/API_SPEC.md) | REST API 엔드포인트 명세 |
| [DB 설계서](./docs/DATABASE_DESIGN.md) | 데이터베이스 스키마 설계 |
| [아키텍처 문서](./docs/ARCHITECTURE.md) | 시스템 아키텍처 설명 |
| [인증 정책](./docs/AUTH_POLICY.md) | 인증/인가 정책 및 보안 |
| [에러 명세서](./docs/ERROR_SPEC.md) | 에러 코드 및 응답 형식 |
| [배포 가이드](./docs/DEPLOYMENT.md) | 배포 및 운영 가이드 |

---

## 🔗 API

### Base URL
- Development: `http://localhost:8080/api`

### 주요 엔드포인트

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/auth/signup` | 회원가입 | - |
| `POST` | `/auth/login` | 로그인 | - |
| `POST` | `/auth/refresh` | 토큰 갱신 | - |
| `GET` | `/auth/check-login-id` | 아이디 중복 확인 | - |
| `GET` | `/stocks` | 주식 목록 조회 | ✅ |
| `GET` | `/stocks/{code}` | 주식 상세 조회 | ✅ |
| `GET` | `/stocks/search` | 주식 검색 | ✅ |
| `GET` | `/stocks/top` | 인기 종목 | ✅ |
| `GET` | `/stocks/{code}/orderbook` | 호가 조회 | ✅ |
| `POST` | `/orders` | 주문 생성 (시장가 즉시 체결 / 지정가 대기) | ✅ |
| `GET` | `/orders` | 주문 내역 | ✅ |
| `GET` | `/orders/{id}` | 주문 상세 | ✅ |
| `GET` | `/orders/pending` | 미체결 주문 조회 | ✅ |
| `PATCH` | `/orders/{id}/cancel` | 주문 취소 | ✅ |
| `GET` | `/orders/stream` | 체결 알림 스트림 (SSE) | ✅ |
| `GET` | `/stocks/stream` | 시세 스트림 (SSE) | - |
| `GET` | `/portfolio` | 포트폴리오 조회 | ✅ |
| `GET` | `/stocks/watchlist` | 관심종목 조회 | ✅ |
| `POST` | `/stocks/watchlist` | 관심종목 추가 | ✅ |
| `DELETE` | `/stocks/watchlist/{code}` | 관심종목 삭제 | ✅ |
| `GET` | `/wmti/questions` | WMTI 문항 조회 | ✅ |
| `POST` | `/wmti/submit` | WMTI 제출 | ✅ |
| `GET` | `/wmti/result` | WMTI 결과 조회 | ✅ |
| `GET` | `/rewards/level` | 레벨 정보 | ✅ |
| `GET` | `/rewards/achievements` | 업적 목록 | ✅ |
| `GET` | `/users/me` | 내 정보 조회 | ✅ |
| `PATCH` | `/users/me` | 프로필 수정 | ✅ |
| `DELETE` | `/users/me` | 회원 탈퇴 | ✅ |
| `GET` | `/health` | 서버 생존 확인 | - |

---

## 🧩 기술적 의사결정

모의투자 서비스의 핵심은 **"돈이 틀리지 않는 것"** 입니다.
잔액 도메인을 설계하면서 마주친 문제와 선택의 근거를 정리했습니다.

### 1. 동시 주문에서 잔액 정합성 지키기

한 사용자가 여러 탭에서 동시에 주문하거나 요청이 중복 전송되면, 잔액을 읽고 쓰는 사이에
다른 트랜잭션이 끼어들어 **갱신 유실(lost update)** 이 발생합니다. 잔액이 음수가 되거나
차감이 누락될 수 있습니다.

**검증 방법** — 32개 스레드를 `CountDownLatch` 게이트로 동시에 출발시켜 같은 유저로 매수합니다.

```
잔액 비음수 | 차감 합계 == 체결 금액 합계 | 이력 합계 == 차감 합계 | 보유 수량 == 체결 수량 합계
```

**선택: 낙관적 락(`@Version`) + `@Retryable` 재시도**

| | 낙관적 락 + 재시도 | 비관적 락 (`SELECT ... FOR UPDATE`) |
|---|---|---|
| 충돌이 없을 때 | 락 오버헤드 없음 | 매 주문마다 행 락 획득 |
| 충돌이 있을 때 | 롤백 후 재실행 | 대기 후 순차 진행 |
| 데드락 | 없음 | 락 획득 순서에 따라 가능 |

잠금 대상이 **"한 사용자의 잔액"** 인데, 한 사람이 같은 순간에 여러 주문을 넣는 일은 드뭅니다.
충돌 빈도가 낮은 곳에 비관적 락을 걸면 거의 일어나지 않을 충돌을 막으려고 모든 주문에 락 비용을
물리는 셈이라 낙관적 락을 택했습니다.

두 전략을 같은 테스트로 실측 비교할 수 있도록 `OrderUserFinder` 인터페이스로 조회 지점을
추상화하고, `investory.order.lock-mode` 프로퍼티 하나로 전환되게 했습니다.

```bash
ORDER_LOCK_MODE=pessimistic ./gradlew test --tests ConcurrentOrderIntegrationTest
```

> 💡 **재시도는 트랜잭션 바깥에 있어야 합니다.** 낙관적 락 충돌이 나는 순간 그 트랜잭션은 이미
> rollback-only 로 마킹되고 영속성 컨텍스트도 오염된 상태라, 같은 트랜잭션 안에서 재시도해 봐야
> 커밋할 수 없습니다. 그래서 재시도 경계(`OrderService`)와 트랜잭션 경계(`OrderPlacer`)를
> 서로 다른 빈으로 분리했습니다.

### 2. 셀프 인보케이션 제거

기존 `OrderService.createOrder` 는 같은 클래스의 `@Transactional executeOrder` 를 직접 호출했습니다.
스프링 AOP 는 프록시 기반이라 이 내부 호출은 프록시를 타지 않고, **애노테이션이 아무 효과 없이
붙어만 있는 상태**였습니다. 호출자가 이미 트랜잭션 안이라 동작에는 문제가 없었지만, 코드가
거짓말을 하고 있어 나중에 트랜잭션 경계를 조정할 때 사고가 날 수 있는 구조였습니다.

체결 로직을 `OrderExecutor` 컴포넌트로 분리해 호출이 반드시 프록시를 경유하게 만들었습니다.

```
OrderService   (@Retryable, 트랜잭션 없음)   ← 재시도 경계
  └ OrderPlacer   (@Transactional)           ← 트랜잭션 경계
       └ OrderExecutor                        ← 체결
            ├ REQUIRED      : 시장가 — 주문 저장과 체결을 원자적으로 묶음
            └ REQUIRES_NEW  : 지정가 — 주문 1건당 트랜잭션 격리
```

### 3. 게이미피케이션 결합 분리

체결 로직 안에 `user.addExperience(20)` 이 직접 박혀 있어, 주문 도메인이 보상 정책을 알고 있었습니다.
`OrderFilledEvent` 발행으로 바꾸고 reward 도메인이 `@TransactionalEventListener(AFTER_COMMIT)` 로
수신하도록 분리했습니다. 이제 업적 해금이나 알림 발송이 추가돼도 주문 코드는 바뀌지 않습니다.

**`AFTER_COMMIT` 을 쓴 이유** — 체결 트랜잭션이 롤백되면 경험치도 없던 일이 되어야 합니다.
일반 `@EventListener` 나 `BEFORE_COMMIT` 이면 롤백된 주문에도 경험치가 올라갑니다.

**`REQUIRES_NEW` 가 필요한 이유** — `AFTER_COMMIT` 리스너는 원본 트랜잭션이 **이미 커밋된**
상태에서 호출됩니다. 트랜잭션 동기화는 살아 있지만 커밋 지점은 지났기 때문에, 여기서 엔티티를
수정해도 flush 되지 않고 조용히 사라집니다. 새 트랜잭션을 명시적으로 열어야 저장됩니다.

**경험치 적립에도 재시도를 붙인 이유** — 경험치 적립도 결국 `@Version` 이 걸린 같은 User 행에 대한
UPDATE 입니다. 동시 주문이 몰리면 체결이 아니라 **적립 단계에서** 충돌이 나서, 체결은 성공했는데
경험치만 조용히 유실됩니다.

### 4. 지정가 주문 매칭

시세 갱신 → 매칭 → 체결 → 알림이 하나의 흐름으로 이어지도록 이벤트로 연결했습니다.

```
StockPriceSimulator  ──publish──>  StockPriceUpdatedEvent
   (@Scheduled)                          │  AFTER_COMMIT
                                         ├──> LimitOrderMatcher ──> OrderExecutor
                                         └──> SseService (시세 push)
                                                                        │
                                              OrderFilledEvent <────────┘
                                                    ├──> ExperienceGranter
                                                    └──> SseService (체결 알림 push)
```

| 문제 | 선택 | 근거 |
|------|------|------|
| 대량 주문 페이징 | **keyset 페이징** (`id > lastSeenId`) | 체결된 주문이 PENDING 에서 빠지면 offset 방식은 뒤 페이지가 당겨져 **주문을 건너뛴다** |
| 일부 체결 실패 | 매처는 트랜잭션 없음, **주문 1건당 트랜잭션** | 100건 중 마지막 1건이 실패했다고 앞의 99건이 롤백되면 안 된다 |
| 체결 시점 잔액 부족 | PENDING 유지 대신 **REJECTED** | 영원히 체결 안 되는 좀비 주문이 쌓이는 것을 막는다 |
| 체결 조건 판정 위치 | **엔티티** (`Order#isMatchable`) | 매처 없이도 단위 테스트가 가능하다 |
| 스케줄 방식 | `fixedRate` 대신 **`fixedDelay`** | 한 틱이 주기보다 오래 걸리면 실행이 누적돼 커넥션을 소진한다 |

### 5. 발견하고 고친 버그

**시세 변동률이 "전일 대비"가 아니었던 문제**

`Stock.updatePrice` 가 갱신할 때마다 `previousClose = currentPrice` 로 덮어써서, 변동률이
전일 대비가 아니라 **직전 틱 대비**로 계산되고 있었습니다. 초 단위로 갱신되는 시뮬레이터를 붙이면
변동률이 항상 ±0.5% 언저리에 머물러 화면상 의미가 사라집니다. `previousClose` 는 일자 롤오버
시점에만 바뀌도록 `rollOverDay()` 로 분리했습니다.

---

## 📡 실시간 처리

시세와 체결 알림은 SSE(Server-Sent Events)로 전달합니다.

| 엔드포인트 | 채널 | 인증 |
|-----------|------|------|
| `GET /api/stocks/stream` | 전 종목 시세 브로드캐스트 | - |
| `GET /api/orders/stream` | 본인 주문 체결 알림 | ✅ |

**WebSocket 대신 SSE 를 택한 이유** — 이 서비스의 실시간 통신은 서버 → 클라이언트 단방향입니다.
주문은 REST 로 충분하고, 양방향 채널이 필요한 요구사항이 없습니다. SSE 는 HTTP 위에서 동작해
별도 프로토콜 협상이 없고 브라우저가 자동 재연결까지 처리해 줍니다.

> ⚠️ 브라우저 기본 `EventSource` 는 커스텀 헤더를 붙일 수 없어, Authorization 헤더로 인증하는
> `/api/orders/stream` 은 `@microsoft/fetch-event-source` 같은 fetch 기반 폴리필이 필요합니다.
> 토큰을 쿼리 파라미터로 받는 방법도 있지만 접근 로그와 Referer 헤더에 토큰이 남아 채택하지 않았습니다.

---

## 🗺️ 로드맵

현재 구조의 한계와, 그것을 알면서도 지금 하지 않기로 한 이유를 함께 적었습니다.

| 항목 | 내용 | 우선순위 |
|------|------|----------|
| **Flyway 도입** | 현재 `ddl-auto=update` + 애플리케이션 레벨 데이터 백필. 기존 스키마 베이스라인을 잡고 마이그레이션을 버전 관리해야 함 | 높음 |
| **증거금(예약) 방식** | 지정가 매수 접수 시 현금을 미리 묶어 두는 방식. 현재는 체결 시점에 잔액을 확인하고 부족하면 REJECTED | 중간 |
| **SSE 다중 인스턴스 대응** | Emitter 저장소가 인메모리라 단일 인스턴스 전용. 스케일 아웃하려면 Redis Pub/Sub 으로 인스턴스 간 브로드캐스트가 필요 | 중간 |
| **한국투자증권 오픈API 연동** | 시뮬레이터를 실거래소 시세 피드로 교체. 현재 구조상 가격 피드만 갈아끼우면 됨 | 낮음 |
| **부분 체결** | 현재는 전량 체결만 지원. `Order.filledQuantity` 는 이미 부분 체결을 표현할 수 있게 설계되어 있음 | 낮음 |
| **경험치 적립 아웃박스** | 재시도 소진 시 경험치가 유실될 수 있음. 유실량이 문제되면 아웃박스 테이블로 승격 | 낮음 |

---

## 🧪 Testing

```bash
cd backend && ./gradlew test     # 백엔드
cd frontend && npm run test      # 프론트엔드
```

통합 테스트는 H2 인메모리 DB(MySQL 호환 모드)로 실행되며, 별도 DB나 Docker 없이 바로 돌아갑니다.

| 테스트 | 검증 내용 |
|--------|-----------|
| `CashBalanceIntegrationTest` | 잔액 부족 매수 거부 · 매수 차감/이력 정합 · 매도 잔액 증가 · 이력 합계와 잔액 변화 일치 |
| `OptimisticLockConflictTest` | **실패 재현**. 재시도 계층을 우회해 `@Version` 만으로는 충돌이 그대로 노출됨을 증명 |
| `ConcurrentOrderIntegrationTest` | 32스레드 동시 매수에서 잔액·이력·보유 수량 정합, 오버드로우 방지 |
| `OrderFilledEventTest` | 이벤트 발행/미발행 조건과 `AFTER_COMMIT` 리스너의 실제 적립 |
| `LimitOrderMatchingTest` | 지정가 경계값 체결 · 취소 주문 제외 · 잔액 부족 시 REJECTED · 변동률 계산 |

> 💡 **실패를 재현하는 테스트를 남겨 둔 이유**
> 재시도를 붙인 뒤 테스트가 통과하면 "원래 문제가 있긴 했나?"를 알 수 없습니다.
> `OptimisticLockConflictTest` 가 있어야 나중에 누가 `@Retryable` 을 지웠을 때
> 무엇이 왜 깨지는지가 코드로 설명됩니다.

### 락 전략 비교 실험

같은 동시성 테스트를 두 전략으로 돌려 소요 시간과 재시도 횟수를 비교할 수 있습니다.

```bash
./gradlew test --tests ConcurrentOrderIntegrationTest                      # 낙관적 락 (기본)
ORDER_LOCK_MODE=pessimistic ./gradlew test --tests ConcurrentOrderIntegrationTest   # 비관적 락
```

테스트가 아래 리포트를 출력합니다. 낙관적 락이면 시도 횟수 > 32 (재시도 발생),
비관적 락이면 == 32 여야 합니다.

```
===== 동시성 실험 리포트 =====
락 전략      : OPTIMISTIC_LOCK_WITH_RETRY
스레드 수    : 32
총 소요 시간 : ... ms
총 시도 횟수 : ... (재시도 ...회)
성공/실패    : 32/0
최종 잔액    : ...
============================
```

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Commit Convention

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 코드 추가
chore: 빌드 설정 변경
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Made with ❤️ by Investory Team**

</div>
