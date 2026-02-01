# 📡 API 명세서 (API Specification)

## 1. 개요

### Base URL
```
Development: http://localhost:8080/api
Production:  https://api.investory.com/api
```

### 공통 헤더
| Header | Value | Description |
|--------|-------|-------------|
| `Content-Type` | `application/json` | 요청 본문 형식 |
| `Authorization` | `Bearer {accessToken}` | 인증 토큰 (인증 필요 API) |

### 공통 응답 형식
```json
{
  "success": true,
  "data": { ... },
  "message": "성공"
}
```

### 에러 응답 형식
```json
{
  "success": false,
  "error": {
    "code": "U001",
    "message": "이미 존재하는 아이디입니다."
  }
}
```

---

## 2. 인증 API

### 2.1 회원가입
```
POST /auth/signup
```

**Request Body**
```json
{
  "loginId": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "nickname": "테스터",
  "phone": "010-1234-5678"
}
```

**Validation**
| Field | Rules |
|-------|-------|
| loginId | 필수, 4~20자 |
| password | 필수, 8~20자 |
| email | 필수, 이메일 형식 |
| nickname | 필수, 2~10자 |
| phone | 선택 |

**Response (201 Created)**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "loginId": "testuser",
    "email": "test@example.com",
    "nickname": "테스터"
  },
  "message": "회원가입이 완료되었습니다."
}
```

---

### 2.2 로그인
```
POST /auth/login
```

**Request Body**
```json
{
  "loginId": "testuser",
  "password": "password123"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userId": 1,
      "loginId": "testuser",
      "email": "test@example.com",
      "nickname": "테스터",
      "level": 1,
      "experience": 0,
      "wmtiType": null
    }
  },
  "message": "로그인 성공"
}
```

---

### 2.3 토큰 갱신
```
POST /auth/refresh
```

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "message": "토큰이 갱신되었습니다."
}
```

---

### 2.4 아이디 중복 확인
```
GET /auth/check-login-id?loginId={loginId}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "available": true
  },
  "message": "사용 가능한 아이디입니다."
}
```

---

### 2.5 이메일 중복 확인
```
GET /auth/check-email?email={email}
```

---

## 3. 사용자 API

### 3.1 프로필 조회
```
GET /users/me
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "loginId": "testuser",
    "email": "test@example.com",
    "nickname": "테스터",
    "phone": "010-1234-5678",
    "profileImage": null,
    "level": 3,
    "experience": 450,
    "wmtiType": "ENTJ",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

---

### 3.2 프로필 수정
```
PUT /users/me
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "nickname": "새닉네임",
  "phone": "010-9999-8888",
  "profileImage": "https://example.com/image.jpg"
}
```

---

### 3.3 비밀번호 변경
```
PUT /users/me/password
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword456"
}
```

---

## 4. 주식 API

### 4.1 주식 목록 조회
```
GET /stocks?market={market}&sector={sector}&keyword={keyword}&page={page}&size={size}
```

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| market | string | N | KOSPI, KOSDAQ, NASDAQ, NYSE, AMEX |
| sector | string | N | 섹터 필터 |
| keyword | string | N | 종목명/코드 검색 |
| page | int | N | 페이지 번호 (0부터, 기본값: 0) |
| size | int | N | 페이지 크기 (기본값: 20) |

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "code": "005930",
        "name": "삼성전자",
        "englishName": "Samsung Electronics",
        "market": "KOSPI",
        "sector": "전기전자",
        "currentPrice": 71500.00,
        "changeRate": 1.42,
        "changeAmount": 1000.00,
        "volume": 15234567
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "number": 0,
    "size": 20
  }
}
```

---

### 4.2 주식 상세 조회
```
GET /stocks/{code}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "code": "005930",
    "name": "삼성전자",
    "englishName": "Samsung Electronics",
    "market": "KOSPI",
    "sector": "전기전자",
    "currentPrice": 71500.00,
    "previousClose": 70500.00,
    "changeRate": 1.42,
    "changeAmount": 1000.00,
    "high52Week": 80000.00,
    "low52Week": 55000.00,
    "volume": 15234567,
    "marketCap": 4268500000000,
    "logoUrl": "https://example.com/samsung-logo.png"
  }
}
```

---

### 4.3 호가 조회
```
GET /stocks/{code}/orderbook
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "stockCode": "005930",
    "stockName": "삼성전자",
    "currentPrice": 71500.00,
    "askOrders": [
      { "price": 71600.00, "quantity": 5000 },
      { "price": 71700.00, "quantity": 3200 },
      ...
    ],
    "bidOrders": [
      { "price": 71500.00, "quantity": 8000 },
      { "price": 71400.00, "quantity": 4500 },
      ...
    ]
  }
}
```

---

### 4.4 관심종목 토글
```
POST /stocks/{code}/watchlist
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "isWatched": true
  },
  "message": "관심종목에 추가되었습니다."
}
```

---

### 4.5 관심종목 목록 조회
```
GET /stocks/watchlist
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": [
    {
      "code": "005930",
      "name": "삼성전자",
      "market": "KOSPI",
      "currentPrice": 71500.00,
      "changeRate": 1.42,
      "addedAt": "2024-01-15T10:30:00"
    }
  ]
}
```

---

## 5. 주문 API

### 5.1 주문 생성
```
POST /orders
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "stockCode": "005930",
  "orderType": "MARKET",
  "side": "BUY",
  "quantity": 10,
  "price": null
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| stockCode | string | Y | 종목 코드 |
| orderType | enum | Y | MARKET(시장가), LIMIT(지정가) |
| side | enum | Y | BUY(매수), SELL(매도) |
| quantity | int | Y | 수량 (1 이상) |
| price | decimal | N | 지정가인 경우 필수 |

**Response (201 Created)**
```json
{
  "success": true,
  "data": {
    "orderId": 1,
    "stockCode": "005930",
    "stockName": "삼성전자",
    "orderType": "MARKET",
    "side": "BUY",
    "status": "FILLED",
    "quantity": 10,
    "filledQuantity": 10,
    "price": null,
    "filledPrice": 71500.00,
    "totalAmount": 715000.00,
    "createdAt": "2024-01-15T14:30:00",
    "filledAt": "2024-01-15T14:30:00"
  },
  "message": "주문이 체결되었습니다."
}
```

---

### 5.2 주문 취소
```
POST /orders/{orderId}/cancel
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "reason": "주문 실수"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "orderId": 1,
    "status": "CANCELLED",
    "cancelReason": "주문 실수",
    "cancelledAt": "2024-01-15T14:35:00"
  },
  "message": "주문이 취소되었습니다."
}
```

---

### 5.3 주문 내역 조회
```
GET /orders?status={status}&page={page}&size={size}
Authorization: Bearer {accessToken}
```

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | N | PENDING, FILLED, CANCELLED |
| page | int | N | 페이지 번호 |
| size | int | N | 페이지 크기 |

---

### 5.4 주문 상세 조회
```
GET /orders/{orderId}
Authorization: Bearer {accessToken}
```

---

## 6. 포트폴리오 API

### 6.1 포트폴리오 조회
```
GET /portfolio
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "totalValue": 5000000.00,
    "totalInvestment": 4500000.00,
    "totalProfitLoss": 500000.00,
    "totalProfitLossRate": 11.11,
    "holdings": [
      {
        "stockCode": "005930",
        "stockName": "삼성전자",
        "market": "KOSPI",
        "quantity": 50,
        "averagePrice": 68000.00,
        "currentPrice": 71500.00,
        "currentValue": 3575000.00,
        "profitLoss": 175000.00,
        "profitLossRate": 5.15
      }
    ]
  }
}
```

---

### 6.2 포트폴리오 요약
```
GET /portfolio/summary
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "totalValue": 5000000.00,
    "totalProfitLoss": 500000.00,
    "totalProfitLossRate": 11.11,
    "holdingsCount": 5
  }
}
```

---

## 7. WMTI API

### 7.1 문항 조회
```
GET /wmti/questions
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "totalQuestions": 20,
    "questions": [
      {
        "id": 1,
        "question": "투자 결정을 내릴 때 당신은?",
        "optionA": "다양한 사람들의 의견을 듣고 결정한다",
        "optionB": "혼자서 충분히 분석하고 결정한다",
        "category": "EI"
      }
    ]
  }
}
```

---

### 7.2 테스트 제출
```
POST /wmti/submit
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "answers": [
    { "questionId": 1, "answer": "A" },
    { "questionId": 2, "answer": "B" },
    ...
  ]
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "wmtiType": "ENTJ",
    "typeName": "전략적 투자자",
    "description": "장기적인 관점에서 체계적으로 투자 전략을 수립합니다.",
    "characteristics": [
      "명확한 투자 목표 설정",
      "체계적인 포트폴리오 관리",
      "리스크 대비 수익 분석 중시"
    ],
    "investmentStyle": "가치 투자와 성장 투자의 균형",
    "recommendations": [
      "분산 투자로 리스크 관리",
      "장기 투자 관점 유지",
      "정기적인 포트폴리오 리밸런싱"
    ],
    "testDate": "2024-01-15T14:30:00"
  },
  "message": "WMTI 분석이 완료되었습니다."
}
```

---

### 7.3 결과 조회
```
GET /wmti/result
Authorization: Bearer {accessToken}
```

---

## 8. 리워드 API

### 8.1 레벨 정보 조회
```
GET /rewards/level
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "currentLevel": 3,
    "currentExp": 450,
    "nextLevelExp": 600,
    "progress": 75.0,
    "expToNextLevel": 150
  }
}
```

---

### 8.2 업적 목록 조회
```
GET /rewards/achievements
Authorization: Bearer {accessToken}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": [
    {
      "achievementId": "FIRST_TRADE",
      "name": "첫 거래",
      "description": "첫 번째 주문을 체결하세요",
      "iconUrl": "https://example.com/icons/first-trade.png",
      "rewardExp": 50,
      "isAchieved": true,
      "achievedAt": "2024-01-10T09:15:00"
    },
    {
      "achievementId": "TRADE_10",
      "name": "주린이 탈출",
      "description": "10회 거래를 완료하세요",
      "iconUrl": "https://example.com/icons/trade-10.png",
      "rewardExp": 100,
      "isAchieved": false,
      "achievedAt": null
    }
  ]
}
```

---

## 9. 에러 코드

### 인증 에러 (A)
| Code | Message | HTTP Status |
|------|---------|-------------|
| A001 | 토큰이 만료되었습니다 | 401 |
| A002 | 유효하지 않은 토큰입니다 | 401 |
| A003 | 비밀번호가 일치하지 않습니다 | 401 |
| A004 | 인증이 필요합니다 | 401 |
| A005 | 접근 권한이 없습니다 | 403 |

### 사용자 에러 (U)
| Code | Message | HTTP Status |
|------|---------|-------------|
| U001 | 이미 존재하는 아이디입니다 | 409 |
| U002 | 이미 존재하는 이메일입니다 | 409 |
| U003 | 사용자를 찾을 수 없습니다 | 404 |

### 주식 에러 (S)
| Code | Message | HTTP Status |
|------|---------|-------------|
| S001 | 종목을 찾을 수 없습니다 | 404 |

### 주문 에러 (O)
| Code | Message | HTTP Status |
|------|---------|-------------|
| O001 | 잔고가 부족합니다 | 400 |
| O002 | 보유 수량이 부족합니다 | 400 |
| O003 | 이미 체결된 주문입니다 | 400 |
| O004 | 주문을 찾을 수 없습니다 | 404 |
