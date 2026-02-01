# 🗄️ 데이터베이스 설계 문서 (Database Design)

## 1. 개요

### 1.1 데이터베이스 정보
| 항목 | 내용 |
|------|------|
| DBMS | MySQL 8.0 |
| Character Set | utf8mb4 |
| Collation | utf8mb4_unicode_ci |
| 스키마명 | investory |

### 1.2 설계 원칙
- 3차 정규화 적용
- 낙관적 락(Optimistic Lock)을 통한 동시성 제어
- 인덱스를 통한 조회 성능 최적화
- Soft Delete 패턴 적용 (is_active 필드)

---

## 2. ERD (Entity Relationship Diagram)

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    users     │       │    stocks    │       │ achievements │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ PK id        │       │ PK id        │       │ PK id        │
│    login_id  │       │    code      │       │    code      │
│    password  │       │    name      │       │    name      │
│    email     │       │    market    │       │    description│
│    nickname  │       │    sector    │       │    icon_url  │
│    level     │       │    current_  │       │    reward_exp│
│    experience│       │    price     │       │    condition │
│    wmti_type │       │    ...       │       └──────────────┘
└──────┬───────┘       └──────┬───────┘               │
       │                      │                       │
       │    ┌─────────────────┴───────────────┐      │
       │    │                                 │      │
       ▼    ▼                                 ▼      ▼
┌──────────────┐       ┌──────────────┐  ┌────────────────┐
│  watchlists  │       │   holdings   │  │user_achievements│
├──────────────┤       ├──────────────┤  ├────────────────┤
│ PK id        │       │ PK id        │  │ PK id          │
│ FK user_id   │       │ FK user_id   │  │ FK user_id     │
│ FK stock_id  │       │ FK stock_id  │  │ FK achievement │
└──────────────┘       │    quantity  │  │    _id         │
                       │    average_  │  │    achieved_at │
       ┌───────────────┤    price     │  └────────────────┘
       │               └──────────────┘
       │
       ▼
┌──────────────┐       ┌──────────────┐
│    orders    │       │ wmti_results │
├──────────────┤       ├──────────────┤
│ PK id        │       │ PK id        │
│ FK user_id   │       │ FK user_id   │
│ FK stock_id  │       │    wmti_type │
│    order_type│       │    e_score   │
│    side      │       │    s_score   │
│    status    │       │    t_score   │
│    quantity  │       │    j_score   │
│    price     │       │    answers   │
│    ...       │       └──────────────┘
└──────────────┘
```

---

## 3. 테이블 정의

### 3.1 users (사용자)

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version BIGINT DEFAULT 0,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(30) NOT NULL,
    phone VARCHAR(20),
    profile_image VARCHAR(500),
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    provider ENUM('LOCAL', 'GOOGLE', 'KAKAO', 'NAVER') NOT NULL DEFAULT 'LOCAL',
    provider_id VARCHAR(100),
    level INT NOT NULL DEFAULT 1,
    experience INT NOT NULL DEFAULT 0,
    wmti_type VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_login_id (login_id),
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO | PK |
| version | BIGINT | Y | 0 | 낙관적 락 버전 |
| login_id | VARCHAR(50) | N | - | 로그인 아이디 |
| password | VARCHAR(255) | N | - | BCrypt 암호화 비밀번호 |
| email | VARCHAR(100) | N | - | 이메일 |
| nickname | VARCHAR(30) | N | - | 닉네임 |
| phone | VARCHAR(20) | Y | - | 전화번호 |
| profile_image | VARCHAR(500) | Y | - | 프로필 이미지 URL |
| role | ENUM | N | USER | 권한 (USER, ADMIN) |
| provider | ENUM | N | LOCAL | 인증 제공자 |
| provider_id | VARCHAR(100) | Y | - | 소셜 로그인 ID |
| level | INT | N | 1 | 사용자 레벨 |
| experience | INT | N | 0 | 경험치 |
| wmti_type | VARCHAR(20) | Y | - | WMTI 유형 |
| is_active | BOOLEAN | N | TRUE | 활성 상태 |
| last_login_at | DATETIME | Y | - | 마지막 로그인 |
| created_at | DATETIME | N | NOW | 생성일 |
| updated_at | DATETIME | N | NOW | 수정일 |

---

### 3.2 stocks (주식)

```sql
CREATE TABLE stocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version BIGINT DEFAULT 0,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    english_name VARCHAR(100),
    market ENUM('KOSPI', 'KOSDAQ', 'NASDAQ', 'NYSE', 'AMEX') NOT NULL,
    sector VARCHAR(50),
    current_price DECIMAL(20, 2) NOT NULL DEFAULT 0,
    previous_close DECIMAL(20, 2) DEFAULT 0,
    change_rate DECIMAL(10, 2) DEFAULT 0,
    change_amount DECIMAL(20, 2) DEFAULT 0,
    high_52_week DECIMAL(20, 2) DEFAULT 0,
    low_52_week DECIMAL(20, 2) DEFAULT 0,
    volume BIGINT DEFAULT 0,
    market_cap BIGINT DEFAULT 0,
    logo_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_stock_code (code),
    INDEX idx_stock_market (market),
    INDEX idx_stock_sector (sector)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

| 컬럼 | 타입 | NULL | 설명 |
|------|------|------|------|
| id | BIGINT | N | PK |
| code | VARCHAR(20) | N | 종목 코드 |
| name | VARCHAR(100) | N | 종목명 (한글) |
| english_name | VARCHAR(100) | Y | 종목명 (영문) |
| market | ENUM | N | 시장 구분 |
| sector | VARCHAR(50) | Y | 섹터/업종 |
| current_price | DECIMAL(20,2) | N | 현재가 |
| previous_close | DECIMAL(20,2) | Y | 전일 종가 |
| change_rate | DECIMAL(10,2) | Y | 등락률 (%) |
| change_amount | DECIMAL(20,2) | Y | 등락폭 |
| high_52_week | DECIMAL(20,2) | Y | 52주 최고가 |
| low_52_week | DECIMAL(20,2) | Y | 52주 최저가 |
| volume | BIGINT | Y | 거래량 |
| market_cap | BIGINT | Y | 시가총액 |
| logo_url | VARCHAR(500) | Y | 로고 이미지 URL |

---

### 3.3 watchlists (관심종목)

```sql
CREATE TABLE watchlists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_watchlist_user_stock (user_id, stock_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 3.4 holdings (보유종목)

```sql
CREATE TABLE holdings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version BIGINT DEFAULT 0,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    average_price DECIMAL(20, 2) NOT NULL,
    total_investment DECIMAL(20, 2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_holding_user_stock (user_id, stock_id),
    INDEX idx_holding_user (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

| 컬럼 | 타입 | 설명 |
|------|------|------|
| quantity | INT | 보유 수량 |
| average_price | DECIMAL(20,2) | 평균 매입가 |
| total_investment | DECIMAL(20,2) | 총 투자금액 |

---

### 3.5 orders (주문)

```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version BIGINT DEFAULT 0,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    order_type ENUM('MARKET', 'LIMIT') NOT NULL,
    side ENUM('BUY', 'SELL') NOT NULL,
    status ENUM('PENDING', 'PARTIALLY_FILLED', 'FILLED', 'CANCELLED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    quantity INT NOT NULL,
    filled_quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(20, 2),
    filled_price DECIMAL(20, 2),
    total_amount DECIMAL(20, 2),
    filled_at DATETIME,
    cancelled_at DATETIME,
    cancel_reason VARCHAR(200),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_order_user_status (user_id, status),
    INDEX idx_order_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

| 컬럼 | 타입 | 설명 |
|------|------|------|
| order_type | ENUM | MARKET(시장가), LIMIT(지정가) |
| side | ENUM | BUY(매수), SELL(매도) |
| status | ENUM | 주문 상태 |
| quantity | INT | 주문 수량 |
| filled_quantity | INT | 체결 수량 |
| price | DECIMAL | 주문 가격 (지정가) |
| filled_price | DECIMAL | 체결 가격 |
| total_amount | DECIMAL | 총 체결금액 |

---

### 3.6 wmti_results (WMTI 결과)

```sql
CREATE TABLE wmti_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    wmti_type VARCHAR(20) NOT NULL,
    e_score INT NOT NULL DEFAULT 0,
    i_score INT NOT NULL DEFAULT 0,
    s_score INT NOT NULL DEFAULT 0,
    n_score INT NOT NULL DEFAULT 0,
    t_score INT NOT NULL DEFAULT 0,
    f_score INT NOT NULL DEFAULT 0,
    j_score INT NOT NULL DEFAULT 0,
    p_score INT NOT NULL DEFAULT 0,
    answers JSON,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_wmti_user (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 3.7 achievements (업적)

```sql
CREATE TABLE achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    icon_url VARCHAR(500),
    reward_exp INT NOT NULL DEFAULT 0,
    condition_type VARCHAR(50) NOT NULL,
    condition_value INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

### 3.8 user_achievements (사용자 업적)

```sql
CREATE TABLE user_achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    achieved_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_achievement (user_id, achievement_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 4. 인덱스 전략

### 4.1 Primary Key 인덱스
모든 테이블은 `id` 컬럼에 자동 증가 PK 인덱스 적용

### 4.2 Unique 인덱스
| 테이블 | 인덱스명 | 컬럼 |
|--------|---------|------|
| users | uk_users_login_id | login_id |
| users | uk_users_email | email |
| stocks | uk_stocks_code | code |
| watchlists | uk_watchlist_user_stock | (user_id, stock_id) |
| holdings | uk_holding_user_stock | (user_id, stock_id) |
| user_achievements | uk_user_achievement | (user_id, achievement_id) |

### 4.3 일반 인덱스
| 테이블 | 인덱스명 | 컬럼 | 목적 |
|--------|---------|------|------|
| users | idx_user_login_id | login_id | 로그인 조회 |
| users | idx_user_email | email | 이메일 조회 |
| stocks | idx_stock_code | code | 종목 조회 |
| stocks | idx_stock_market | market | 시장별 필터링 |
| stocks | idx_stock_sector | sector | 섹터별 필터링 |
| holdings | idx_holding_user | user_id | 포트폴리오 조회 |
| orders | idx_order_user_status | (user_id, status) | 주문 내역 조회 |
| orders | idx_order_created_at | created_at | 최근 주문 조회 |

---

## 5. 데이터 무결성

### 5.1 외래 키 제약
- CASCADE DELETE: 부모 삭제 시 자식도 삭제
- 사용자 삭제 → 관련 watchlists, holdings, orders, wmti_results, user_achievements 삭제

### 5.2 낙관적 락
- `version` 컬럼을 통한 동시성 제어
- 적용 테이블: users, stocks, holdings, orders

### 5.3 제약 조건
- NOT NULL: 필수 필드
- UNIQUE: 중복 방지
- CHECK: 데이터 유효성 (MySQL 8.0+)

---

## 6. 초기 데이터

### 6.1 샘플 주식 데이터
- 한국 주식 7종목 (삼성전자, SK하이닉스, 네이버, 카카오 등)
- 미국 주식 8종목 (Apple, Microsoft, Google, Amazon 등)

### 6.2 업적 데이터
```sql
INSERT INTO achievements (code, name, description, reward_exp, condition_type, condition_value) VALUES
('FIRST_TRADE', '첫 거래', '첫 번째 주문을 체결하세요', 50, 'TRADE_COUNT', 1),
('TRADE_10', '주린이 탈출', '10회 거래를 완료하세요', 100, 'TRADE_COUNT', 10),
('TRADE_50', '베테랑 트레이더', '50회 거래를 완료하세요', 200, 'TRADE_COUNT', 50),
('PROFIT_10', '수익왕', '10% 이상의 수익을 달성하세요', 150, 'PROFIT_RATE', 10),
('DIVERSIFY', '분산투자 마스터', '5개 이상의 종목을 보유하세요', 100, 'HOLDING_COUNT', 5),
('WMTI_COMPLETE', '나를 알다', 'WMTI 테스트를 완료하세요', 50, 'WMTI_COMPLETE', 1),
('LEVEL_5', '성장 중', '레벨 5를 달성하세요', 100, 'LEVEL', 5),
('WATCHLIST_10', '종목 헌터', '관심종목 10개를 등록하세요', 50, 'WATCHLIST_COUNT', 10);
```
