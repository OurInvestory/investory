# Investory

> 개인 맞춤형 금융 상품 추천 플랫폼

## 📋 프로젝트 소개

Investory는 사용자의 투자 성향을 분석하여 맞춤형 금융 상품을 추천하는 플랫폼입니다.

### 주요 기능
- 🔐 회원가입/로그인 (자체 로그인 + OAuth2.0 소셜 로그인)
- 📊 투자 성향 분석 (WMTI)
- 💰 금융 상품 검색 및 비교
- 📈 맞춤형 상품 추천
- 💬 커뮤니티
- 🤖 GPT 기반 챗봇

## 🛠 기술 스택

### Backend
- Java 21
- Spring Boot 3.4.1
- Spring Security + JWT + OAuth2
- Spring Data JPA + QueryDSL
- MySQL 8.0
- Redis 7

### Frontend
- React 18
- TypeScript
- Vite
- TailwindCSS
- React Query (TanStack Query)
- Zustand
- React Router v7

### Infrastructure
- Docker & Docker Compose
- Nginx

## 📁 프로젝트 구조

```
investory/
├── backend/                 # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── build.gradle
│   └── Dockerfile
├── frontend/                # React 프론트엔드
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── stores/
│   │   └── ...
│   ├── package.json
│   └── Dockerfile
├── docker/                  # Docker 설정
│   └── mysql/
│       └── init/
├── docker-compose.yml       # Production 환경
├── docker-compose.dev.yml   # Development 환경
└── README.md
```

## 🚀 시작하기

### 사전 요구사항
- Docker & Docker Compose
- Node.js 20+ (로컬 개발 시)
- Java 21+ (로컬 개발 시)

### 1. 환경 변수 설정

```bash
# .env.example을 복사하여 .env 파일 생성
cp .env.example .env

# .env 파일을 열어 필요한 값 설정
```

### 2. Docker로 실행 (권장)

#### 개발 환경 (DB만 Docker로 실행)
```bash
# MySQL, Redis 컨테이너 실행
docker-compose -f docker-compose.dev.yml up -d

# 백엔드 실행 (별도 터미널)
cd backend
./gradlew bootRun

# 프론트엔드 실행 (별도 터미널)
cd frontend
npm install
npm run dev
```

#### 전체 Docker 환경
```bash
# 전체 서비스 빌드 및 실행
docker-compose up --build -d

# 로그 확인
docker-compose logs -f
```

### 3. 로컬 개발 환경

#### 백엔드
```bash
cd backend
./gradlew bootRun
```

#### 프론트엔드
```bash
cd frontend
npm install
npm run dev
```

## 🔗 접속 주소

| 서비스 | URL |
|--------|-----|
| Frontend | http://localhost:3000 (개발) / http://localhost (Docker) |
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/api/swagger-ui.html |

## 📝 API 문서

Swagger UI를 통해 API 문서를 확인할 수 있습니다:
- http://localhost:8080/api/swagger-ui.html

## 🧪 테스트

### 백엔드 테스트
```bash
cd backend
./gradlew test
```

### 프론트엔드 테스트
```bash
cd frontend
npm run test
```

## 📦 빌드

### 백엔드 빌드
```bash
cd backend
./gradlew build
```

### 프론트엔드 빌드
```bash
cd frontend
npm run build
```

## 🤝 기여

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

This project is licensed under the MIT License.
```bash
# MySQL + Redis + Backend
docker-compose up -d

# 또는 개발 환경에서
./gradlew bootRun
```

### 3. API 문서 확인
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health