# Docker Compose를 사용한 PostgreSQL 설정 가이드

## 🐳 Docker 설치

### Windows에서 Docker Desktop 설치
1. **Docker Desktop 다운로드**
   - https://www.docker.com/products/docker-desktop/
   - Windows용 Docker Desktop 다운로드 및 설치

2. **설치 후 확인**
   ```bash
   docker --version
   docker-compose --version
   ```

## 🚀 데이터베이스 실행

### 1. Docker Compose로 PostgreSQL 실행
```bash
# 프로젝트 루트 디렉토리에서 실행
docker-compose up -d
```

### 2. 실행 상태 확인
```bash
# 컨테이너 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs postgres
```

### 3. 데이터베이스 연결 테스트
```bash
# PostgreSQL 컨테이너에 직접 접속
docker exec -it library_postgres psql -U library_user -d library_db

# 테이블 확인
\dt

# 샘플 데이터 확인
SELECT * FROM libraries;
SELECT * FROM books;
SELECT * FROM users;
```

## 🌐 pgAdmin 웹 인터페이스

### 접속 정보
- **URL**: http://localhost:8081
- **이메일**: admin@library.com
- **비밀번호**: admin123

### 서버 연결 설정
1. pgAdmin에 로그인
2. "Add New Server" 클릭
3. 서버 정보 입력:
   - **Name**: Library DB
   - **Host**: postgres (또는 localhost)
   - **Port**: 5432
   - **Database**: library_db
   - **Username**: library_user
   - **Password**: library_password

## 🔧 유용한 명령어

### 컨테이너 관리
```bash
# 컨테이너 시작
docker-compose up -d

# 컨테이너 중지
docker-compose down

# 컨테이너와 볼륨 모두 삭제 (데이터 초기화)
docker-compose down -v

# 로그 실시간 확인
docker-compose logs -f postgres

# 컨테이너 재시작
docker-compose restart postgres
```

### 데이터베이스 백업/복원
```bash
# 데이터베이스 백업
docker exec library_postgres pg_dump -U library_user library_db > backup.sql

# 데이터베이스 복원
docker exec -i library_postgres psql -U library_user library_db < backup.sql
```

### 스키마 재초기화
```bash
# 컨테이너와 볼륨 삭제
docker-compose down -v

# 다시 시작 (새로운 데이터베이스 생성)
docker-compose up -d
```

## 🛠 문제 해결

### 포트 충돌
PostgreSQL 포트(5432)가 이미 사용 중인 경우:
```yaml
# docker-compose.yml에서 포트 변경
ports:
  - "5433:5432"  # 호스트 포트를 5433으로 변경
```

### 권한 문제
```bash
# 컨테이너 내부에서 권한 확인
docker exec -it library_postgres psql -U postgres -d library_db
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO library_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO library_user;
```

### 데이터 영속성
- PostgreSQL 데이터는 `postgres_data` 볼륨에 저장됩니다
- 컨테이너를 삭제해도 데이터는 유지됩니다
- 완전히 초기화하려면 `docker-compose down -v` 실행

## 📊 모니터링

### 리소스 사용량 확인
```bash
# 컨테이너 리소스 사용량
docker stats

# 특정 컨테이너 상세 정보
docker inspect library_postgres
```

### 로그 분석
```bash
# PostgreSQL 로그 확인
docker-compose logs postgres

# 에러 로그만 확인
docker-compose logs postgres | grep ERROR
```

## 🔒 보안 고려사항

### 프로덕션 환경
- 기본 비밀번호 변경
- 네트워크 접근 제한
- SSL/TLS 설정
- 정기적인 백업

### 개발 환경
- 현재 설정은 개발용으로 적합
- 로컬 네트워크에서만 접근 가능 