# 🔐 인증 정책 문서 (Authentication Policy)

## 1. 개요

### 1.1 인증 방식
Investory는 JWT(JSON Web Token) 기반의 무상태(Stateless) 인증 방식을 채택합니다.

### 1.2 보안 원칙
- **최소 권한 원칙**: 필요한 최소한의 권한만 부여
- **심층 방어**: 다층적 보안 검증
- **안전한 기본값**: 기본적으로 모든 엔드포인트 보호

---

## 2. JWT 토큰

### 2.1 토큰 종류

| 토큰 | 유효기간 | 용도 |
|------|---------|------|
| Access Token | 1시간 | API 요청 인증 |
| Refresh Token | 7일 | Access Token 갱신 |

### 2.2 토큰 구조

```json
// Access Token Payload
{
  "sub": "1",                    // 사용자 ID
  "loginId": "testuser",         // 로그인 아이디
  "role": "USER",                // 권한
  "iat": 1704067200,             // 발급 시간
  "exp": 1704070800              // 만료 시간
}

// Refresh Token Payload
{
  "sub": "1",                    // 사용자 ID
  "iat": 1704067200,
  "exp": 1704672000
}
```

### 2.3 토큰 설정

```yaml
# application.yml
jwt:
  secret: ${JWT_SECRET}          # 256비트 이상 비밀키
  access-expiration: 3600000     # 1시간 (밀리초)
  refresh-expiration: 604800000  # 7일 (밀리초)
```

---

## 3. 인증 흐름

### 3.1 로그인 흐름

```
1. 사용자 → 로그인 요청 (loginId, password)
2. 서버 → 사용자 검증
3. 서버 → 비밀번호 검증 (BCrypt)
4. 서버 → Access Token, Refresh Token 발급
5. 서버 → 마지막 로그인 시간 업데이트
6. 클라이언트 → 토큰 저장 (메모리/Secure Storage)
```

### 3.2 API 요청 인증 흐름

```
1. 클라이언트 → Authorization: Bearer {accessToken}
2. JwtAuthenticationFilter → 토큰 추출
3. JwtTokenProvider → 토큰 검증
4. 검증 성공 → SecurityContext에 인증 정보 저장
5. 검증 실패 → 401 Unauthorized 응답
```

### 3.3 토큰 갱신 흐름

```
1. Access Token 만료 감지
2. 클라이언트 → POST /auth/refresh (refreshToken)
3. 서버 → Refresh Token 검증
4. 서버 → 새로운 Access Token 발급
5. 클라이언트 → 토큰 교체
```

---

## 4. 엔드포인트 보안 정책

### 4.1 공개 엔드포인트 (인증 불필요)

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| POST | /api/auth/signup | 회원가입 |
| POST | /api/auth/login | 로그인 |
| POST | /api/auth/refresh | 토큰 갱신 |
| GET | /api/auth/check-login-id | 아이디 중복 확인 |
| GET | /api/auth/check-email | 이메일 중복 확인 |
| GET | /api/stocks | 주식 목록 조회 |
| GET | /api/stocks/{code} | 주식 상세 조회 |
| GET | /api/stocks/{code}/orderbook | 호가 조회 |
| GET | /api/wmti/questions | WMTI 문항 조회 |

### 4.2 인증 필요 엔드포인트

| 메서드 | 엔드포인트 | 권한 |
|--------|-----------|------|
| GET | /api/users/me | USER |
| PUT | /api/users/me | USER |
| PUT | /api/users/me/password | USER |
| POST | /api/orders | USER |
| GET | /api/orders | USER |
| POST | /api/orders/{id}/cancel | USER |
| GET | /api/portfolio | USER |
| POST | /api/wmti/submit | USER |
| GET | /api/rewards/** | USER |
| POST | /api/stocks/{code}/watchlist | USER |
| GET | /api/stocks/watchlist | USER |

### 4.3 관리자 전용 엔드포인트

| 메서드 | 엔드포인트 | 권한 |
|--------|-----------|------|
| * | /api/admin/** | ADMIN |
| * | /actuator/** | ADMIN |

---

## 5. 비밀번호 정책

### 5.1 비밀번호 요구사항

| 항목 | 요구사항 |
|------|---------|
| 최소 길이 | 8자 |
| 최대 길이 | 20자 |
| 암호화 | BCrypt (Strength 10) |

### 5.2 BCrypt 암호화

```java
// 암호화
String encoded = passwordEncoder.encode(rawPassword);

// 검증
boolean matches = passwordEncoder.matches(rawPassword, encoded);
```

---

## 6. Spring Security 설정

### 6.1 SecurityFilterChain 구성

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    return http
        .csrf(csrf -> csrf.disable())           // JWT 사용으로 CSRF 비활성화
        .sessionManagement(session -> session
            .sessionCreationPolicy(STATELESS))  // 세션 미사용
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(PUBLIC_URLS).permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

### 6.2 CORS 설정

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
        "http://localhost:3000",          // 개발 환경
        "https://investory.com"           // 운영 환경
    ));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);
    return source;
}
```

---

## 7. 에러 처리

### 7.1 인증 에러 코드

| 코드 | 메시지 | HTTP 상태 |
|------|--------|----------|
| A001 | 토큰이 만료되었습니다 | 401 |
| A002 | 유효하지 않은 토큰입니다 | 401 |
| A003 | 비밀번호가 일치하지 않습니다 | 401 |
| A004 | 인증이 필요합니다 | 401 |
| A005 | 접근 권한이 없습니다 | 403 |

### 7.2 에러 응답 형식

```json
{
  "success": false,
  "error": {
    "code": "A001",
    "message": "토큰이 만료되었습니다"
  }
}
```

---

## 8. 클라이언트 구현 가이드

### 8.1 토큰 저장

```typescript
// 권장: 메모리 + HTTP Only Cookie (Refresh Token)
// 비권장: localStorage (XSS 취약)

// Access Token은 메모리에 저장
let accessToken: string | null = null;

// Refresh Token은 HTTP Only Cookie 또는 Secure Storage
```

### 8.2 Axios 인터셉터

```typescript
// 요청 인터셉터
api.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 응답 인터셉터 (토큰 갱신)
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // A001: 토큰 만료
      if (error.response.data.error.code === 'A001') {
        const newToken = await refreshToken();
        if (newToken) {
          error.config.headers.Authorization = `Bearer ${newToken}`;
          return api.request(error.config);
        }
      }
      // 로그아웃 처리
      logout();
    }
    return Promise.reject(error);
  }
);
```

---

## 9. 보안 모범 사례

### 9.1 Do's ✅

- JWT 비밀키는 환경변수로 관리
- HTTPS 사용 (운영 환경)
- Access Token 유효기간 짧게 설정
- Refresh Token은 안전하게 저장
- 비밀번호는 BCrypt로 암호화
- 민감한 정보는 토큰에 포함하지 않음

### 9.2 Don'ts ❌

- JWT 비밀키를 코드에 하드코딩
- Access Token을 localStorage에 저장
- 비밀번호를 평문으로 저장/전송
- 토큰에 민감한 개인정보 포함
- 무한 토큰 유효기간 설정

---

## 10. 소셜 로그인 (향후 구현)

### 10.1 지원 예정 제공자

| 제공자 | 상태 |
|--------|------|
| Google | 예정 |
| Kakao | 예정 |
| Naver | 예정 |

### 10.2 OAuth2.0 흐름 (예정)

```
1. 클라이언트 → 소셜 로그인 버튼 클릭
2. 클라이언트 → 제공자 인증 페이지로 리다이렉트
3. 사용자 → 제공자에서 인증
4. 제공자 → 인증 코드와 함께 콜백 URL로 리다이렉트
5. 서버 → 인증 코드로 액세스 토큰 교환
6. 서버 → 사용자 정보 조회
7. 서버 → 내부 JWT 토큰 발급
8. 클라이언트 → 토큰 저장 및 인증 완료
```
