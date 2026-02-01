# 🚀 배포 가이드 (Deployment Guide)

## 1. 개요

### 1.1 배포 환경
| 환경 | 설명 | 용도 |
|------|------|------|
| Local | 로컬 개발 환경 | 개발 |
| Development | 개발 서버 | 테스트 |
| Production | 운영 서버 | 실제 서비스 |

### 1.2 시스템 요구사항

| 구성요소 | 최소 사양 | 권장 사양 |
|---------|----------|----------|
| CPU | 2 Core | 4 Core+ |
| Memory | 4 GB | 8 GB+ |
| Storage | 20 GB | 50 GB+ (SSD) |
| OS | Ubuntu 20.04+ | Ubuntu 22.04 LTS |

---

## 2. Docker 배포

### 2.1 사전 요구사항

```bash
# Docker 설치 확인
docker --version
docker-compose --version

# 권장 버전
# Docker: 24.0+
# Docker Compose: 2.20+
```

### 2.2 환경 변수 설정

```bash
# .env 파일 생성
cp .env.example .env

# 필수 환경 변수 설정
cat > .env << EOF
# MySQL
MYSQL_ROOT_PASSWORD=your_secure_root_password
MYSQL_DATABASE=investory
MYSQL_USER=investory
MYSQL_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_256bit_secret_key_here_minimum_32_characters
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

# Spring
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Redis
REDIS_HOST=redis
REDIS_PORT=6379
EOF
```

### 2.3 빌드 및 실행

```bash
# 전체 서비스 빌드 및 실행
docker-compose up -d --build

# 로그 확인
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f backend
docker-compose logs -f frontend

# 서비스 상태 확인
docker-compose ps
```

### 2.4 서비스 관리

```bash
# 서비스 중지
docker-compose stop

# 서비스 재시작
docker-compose restart

# 서비스 종료 및 볼륨 삭제
docker-compose down -v

# 개별 서비스 재빌드
docker-compose up -d --build backend
```

---

## 3. 로컬 개발 환경

### 3.1 백엔드

```bash
# 1. 데이터베이스 실행 (Docker)
docker-compose up -d mysql redis

# 2. 환경 변수 설정
export SPRING_PROFILES_ACTIVE=local
export JWT_SECRET=your_local_jwt_secret_key_32_chars

# 3. 백엔드 실행
cd backend
./gradlew bootRun

# 또는 IDE에서 InvestoryBackendApplication.java 실행
```

### 3.2 프론트엔드

```bash
# 1. 의존성 설치
cd frontend
npm install

# 2. 개발 서버 실행
npm run dev

# 3. 빌드
npm run build
```

---

## 4. 프로덕션 배포

### 4.1 서버 초기 설정

```bash
# 1. 시스템 업데이트
sudo apt update && sudo apt upgrade -y

# 2. Docker 설치
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# 3. Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 4. 방화벽 설정
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

### 4.2 SSL 인증서 설정

```bash
# Certbot 설치
sudo apt install certbot python3-certbot-nginx -y

# 인증서 발급
sudo certbot --nginx -d investory.com -d www.investory.com

# 자동 갱신 확인
sudo certbot renew --dry-run
```

### 4.3 Nginx 설정

```nginx
# /etc/nginx/sites-available/investory
server {
    listen 80;
    server_name investory.com www.investory.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name investory.com www.investory.com;

    ssl_certificate /etc/letsencrypt/live/investory.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/investory.com/privkey.pem;

    # Frontend
    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    # Backend API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header Host $host;
    }
}
```

---

## 5. 모니터링

### 5.1 헬스 체크

```bash
# Backend 헬스 체크
curl http://localhost:8080/actuator/health

# 응답 예시
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

### 5.2 Docker 모니터링

```bash
# 컨테이너 리소스 사용량
docker stats

# 컨테이너 로그
docker logs -f --tail 100 investory-backend

# 디스크 사용량
docker system df
```

### 5.3 로그 관리

```bash
# Docker 로그 크기 제한 (daemon.json)
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
```

---

## 6. 백업

### 6.1 데이터베이스 백업

```bash
# MySQL 백업 스크립트
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR=/backup/mysql

# 백업 실행
docker exec investory-mysql mysqldump -u root -p$MYSQL_ROOT_PASSWORD investory > $BACKUP_DIR/investory_$DATE.sql

# 압축
gzip $BACKUP_DIR/investory_$DATE.sql

# 7일 이전 백업 삭제
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete
```

### 6.2 복원

```bash
# MySQL 복원
gunzip < investory_backup.sql.gz | docker exec -i investory-mysql mysql -u root -p$MYSQL_ROOT_PASSWORD investory
```

---

## 7. 트러블슈팅

### 7.1 일반적인 문제

#### 컨테이너 시작 실패
```bash
# 로그 확인
docker-compose logs backend

# 일반적인 원인:
# 1. 환경 변수 누락
# 2. 포트 충돌
# 3. 의존 서비스 미실행
```

#### 데이터베이스 연결 실패
```bash
# MySQL 컨테이너 상태 확인
docker exec investory-mysql mysqladmin ping -h localhost

# 연결 테스트
docker exec -it investory-mysql mysql -u investory -p
```

#### 메모리 부족
```bash
# 메모리 사용량 확인
free -h
docker stats

# JVM 힙 크기 조정 (docker-compose.yml)
environment:
  - JAVA_OPTS=-Xms512m -Xmx1024m
```

### 7.2 성능 최적화

```bash
# JVM 옵션 최적화
JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# MySQL 튜닝
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
max_connections = 200
```

---

## 8. CI/CD (선택)

### 8.1 GitHub Actions 예시

```yaml
# .github/workflows/deploy.yml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Build and Deploy
        env:
          SSH_KEY: ${{ secrets.SSH_KEY }}
          SERVER_HOST: ${{ secrets.SERVER_HOST }}
        run: |
          # SSH로 서버 접속 후 배포
          ssh -i $SSH_KEY $SERVER_HOST << 'EOF'
            cd /app/investory
            git pull origin main
            docker-compose up -d --build
          EOF
```

---

## 9. 체크리스트

### 9.1 배포 전 체크리스트

- [ ] 환경 변수 설정 완료
- [ ] 비밀키 보안 확인
- [ ] 데이터베이스 백업
- [ ] 헬스 체크 확인
- [ ] 로그 레벨 설정

### 9.2 배포 후 체크리스트

- [ ] 서비스 정상 동작 확인
- [ ] API 응답 확인
- [ ] 로그 에러 확인
- [ ] 모니터링 대시보드 확인
- [ ] SSL 인증서 유효성 확인
