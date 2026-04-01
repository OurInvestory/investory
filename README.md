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
- 📈 **실시간 시세**: 한국(KOSPI, KOSDAQ) 및 미국(NASDAQ, NYSE) 주식 지원

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
- 시장가/지정가 매수·매도 주문 (API 연동)
- 주문 내역 및 체결 현황 조회
- 관심종목 추가/삭제 (API 연동)

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
│       │   ├── user/                 # 사용자 (프로필, 비밀번호 변경)
│       │   ├── stock/                # 주식 (목록, 상세, 검색, 호가)
│       │   ├── portfolio/            # 포트폴리오 (보유종목, 수익률)
│       │   ├── order/                # 주문 (매수/매도, 체결)
│       │   ├── wmti/                 # WMTI (투자 성향 테스트)
│       │   └── reward/               # 리워드 (레벨, 업적)
│       └── global/                   # 공통 설정
│           ├── common/               # 공통 DTO (ApiResponse 등)
│           ├── config/               # 설정 (CORS, Security 등)
│           ├── exception/            # 글로벌 예외 처리
│           └── security/             # JWT 필터, 인증 설정
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
| `POST` | `/orders` | 주문 생성 | ✅ |
| `GET` | `/orders` | 주문 내역 | ✅ |
| `GET` | `/portfolio` | 포트폴리오 조회 | ✅ |
| `GET` | `/watchlist` | 관심종목 조회 | ✅ |
| `POST` | `/watchlist/{code}` | 관심종목 추가 | ✅ |
| `DELETE` | `/watchlist/{code}` | 관심종목 삭제 | ✅ |
| `GET` | `/wmti/questions` | WMTI 문항 조회 | ✅ |
| `POST` | `/wmti/submit` | WMTI 제출 | ✅ |
| `GET` | `/wmti/result` | WMTI 결과 조회 | ✅ |
| `GET` | `/rewards/level` | 레벨 정보 | ✅ |
| `GET` | `/rewards/achievements` | 업적 목록 | ✅ |
| `GET` | `/users/me` | 내 정보 조회 | ✅ |
| `PUT` | `/users/me` | 프로필 수정 | ✅ |
| `DELETE` | `/users/me` | 회원 탈퇴 | ✅ |

---

## 🌐 무료 배포 가이드

### 추천 구성

| 서비스 | 플랫폼 | 무료 티어 |
|--------|--------|-----------|
| **Frontend** | [Vercel](https://vercel.com) | 무제한 배포, 커스텀 도메인 |
| **Backend** | [Render](https://render.com) | 750시간/월 무료 |
| **MySQL** | [PlanetScale](https://planetscale.com) 또는 [Aiven](https://aiven.io) | 5GB 무료 |
| **Redis** | [Upstash](https://upstash.com) | 10,000 요청/일 무료 |

### 대안 구성

| 서비스 | 플랫폼 | 비고 |
|--------|--------|------|
| **Frontend** | [Netlify](https://netlify.com) | 100GB 대역폭/월 |
| **Backend** | [Railway](https://railway.app) | $5 크레딧/월 무료 |
| **Full Stack** | [Fly.io](https://fly.io) | 3개 VM 무료 |
| **DB** | [Neon](https://neon.tech) (PostgreSQL) | 0.5GB 무료 |

### Vercel (Frontend) 배포

```bash
# 1. Vercel CLI 설치
npm i -g vercel

# 2. 프론트엔드 디렉토리에서 배포
cd frontend
vercel

# 3. 환경변수 설정 (Vercel 대시보드에서)
# VITE_API_BASE_URL=https://your-backend.onrender.com/api
```

### Render (Backend) 배포

1. [Render](https://render.com)에서 **Web Service** 생성
2. GitHub 저장소 연결
3. 설정:
   - **Root Directory**: `backend`
   - **Build Command**: `./gradlew build`
   - **Start Command**: `java -jar build/libs/investory-backend-0.0.1-SNAPSHOT.jar`
4. 환경변수 설정 (MySQL, Redis, JWT 등)

### Docker 배포 (Fly.io)

```bash
# 1. Fly CLI 설치 후 로그인
fly auth login

# 2. 앱 생성 및 배포
fly launch
fly deploy
```

---

## 🧪 Testing

### Backend Tests
```bash
cd backend
./gradlew test
```

### Frontend Tests
```bash
cd frontend
npm run test
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
