# 📈 Investory

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)
![React](https://img.shields.io/badge/React-18-61DAFB)
![TypeScript](https://img.shields.io/badge/TypeScript-5.6-blue)

**모의투자와 게이미피케이션을 결합한 투자 학습 플랫폼**

[Features](#-features) • [Tech Stack](#-tech-stack) • [Getting Started](#-getting-started) • [Documentation](#-documentation) • [API](#-api)

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
- 회원가입 및 로그인 (JWT 기반)
- 소셜 로그인 (Google, Kakao, Naver)
- 프로필 관리

### 📊 주식 거래
- 실시간 주식 시세 조회
- 시장가/지정가 주문
- 주문 내역 및 체결 현황
- 관심종목 관리

### 💼 포트폴리오
- 보유 종목 현황
- 손익 분석 및 수익률 추적
- 투자 현황 대시보드

### 🧠 WMTI (투자 성향 분석)
- 20개 문항의 투자 성향 테스트
- 8가지 투자자 유형 분석
- 맞춤형 투자 조언

### 🏆 게이미피케이션
- 레벨 시스템 (경험치 기반)
- 업적 시스템 (8가지 업적)
- 랭킹 시스템

---

## 🛠 Tech Stack

### Backend
| Technology | Version | Description |
|------------|---------|-------------|
| **Java** | 21 | 프로그래밍 언어 |
| **Spring Boot** | 3.4.1 | 애플리케이션 프레임워크 |
| **Spring Security** | 6.x | 인증 및 인가 |
| **Spring Data JPA** | 3.x | ORM 및 데이터 액세스 |
| **JWT** | 0.12.x | 토큰 기반 인증 |
| **MySQL** | 8.0 | 관계형 데이터베이스 |
| **Redis** | 7.x | 캐싱 및 세션 저장소 |

### Frontend
| Technology | Version | Description |
|------------|---------|-------------|
| **React** | 18.3 | UI 라이브러리 |
| **TypeScript** | 5.6 | 타입 시스템 |
| **Vite** | 6.0 | 빌드 도구 |
| **TailwindCSS** | 3.4 | 유틸리티 CSS 프레임워크 |
| **Zustand** | 5.0 | 상태 관리 |
| **React Query** | 5.x | 서버 상태 관리 |
| **React Router** | 7.x | 라우팅 |

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
├── backend/                      # Spring Boot 백엔드
│   └── src/
│       ├── main/
│       │   ├── java/com/investory/backend/
│       │   │   ├── domain/       # 도메인별 패키지
│       │   │   │   ├── auth/     # 인증 도메인
│       │   │   │   ├── user/     # 사용자 도메인
│       │   │   │   ├── stock/    # 주식 도메인
│       │   │   │   ├── portfolio/# 포트폴리오 도메인
│       │   │   │   ├── order/    # 주문 도메인
│       │   │   │   ├── wmti/     # WMTI 도메인
│       │   │   │   └── reward/   # 리워드 도메인
│       │   │   └── global/       # 공통 설정
│       │   │       ├── common/   # 공통 DTO
│       │   │       ├── config/   # 설정 클래스
│       │   │       ├── exception/# 예외 처리
│       │   │       └── security/ # 보안 설정
│       │   └── resources/
│       └── test/
├── frontend/                     # React 프론트엔드
│   └── src/
│       ├── components/           # 공통 컴포넌트
│       ├── pages/                # 페이지 컴포넌트
│       ├── services/             # API 서비스
│       ├── stores/               # Zustand 스토어
│       └── types/                # TypeScript 타입
├── docs/                         # 문서
├── .github/                      # GitHub 설정
│   ├── ISSUE_TEMPLATE/
│   └── PULL_REQUEST_TEMPLATE.md
├── docker-compose.yml
├── Dockerfile
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
# Swagger UI: http://localhost:8080/api/swagger-ui.html
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
- Production: `https://api.investory.com/api`

### 주요 엔드포인트

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/signup` | 회원가입 |
| `POST` | `/auth/login` | 로그인 |
| `GET` | `/stocks` | 주식 목록 조회 |
| `GET` | `/stocks/{code}` | 주식 상세 조회 |
| `POST` | `/orders` | 주문 생성 |
| `GET` | `/portfolio` | 포트폴리오 조회 |
| `POST` | `/wmti/submit` | WMTI 테스트 제출 |

📝 전체 API 문서: [Swagger UI](http://localhost:8080/api/swagger-ui.html)

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

프로젝트에 기여해주셔서 감사합니다!

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
