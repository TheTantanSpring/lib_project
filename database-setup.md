# PostgreSQL 설치 및 데이터베이스 설정 가이드

## 1. PostgreSQL 설치

### Windows에서 PostgreSQL 설치

1. **PostgreSQL 공식 웹사이트에서 다운로드**
   - https://www.postgresql.org/download/windows/
   - PostgreSQL 15 또는 16 버전 다운로드

2. **설치 과정**
   - 설치 시 다음 정보를 기억하세요:
     - PostgreSQL superuser (postgres) 비밀번호
     - 포트 번호 (기본값: 5432)
     - 설치 디렉토리

3. **환경 변수 설정**
   - PostgreSQL bin 디렉토리를 PATH에 추가
   - 예: `C:\Program Files\PostgreSQL\15\bin`

## 2. 데이터베이스 생성

PostgreSQL이 설치된 후, 다음 명령어들을 실행하세요:

### 2.1 PostgreSQL에 접속
```bash
psql -U postgres
```

### 2.2 데이터베이스 및 사용자 생성
```sql
-- 데이터베이스 생성
CREATE DATABASE library_db;

-- 사용자 생성
CREATE USER library_user WITH PASSWORD 'library_password';

-- 사용자에게 권한 부여
GRANT ALL PRIVILEGES ON DATABASE library_db TO library_user;

-- 데이터베이스 연결
\c library_db

-- 스키마 권한 부여
GRANT ALL ON SCHEMA public TO library_user;
```

### 2.3 테이블 생성 스크립트 실행
```sql
-- Users 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Libraries 테이블
CREATE TABLE libraries (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    opening_hours TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Books 테이블
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    library_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20),
    publisher VARCHAR(255),
    publication_year INTEGER,
    category VARCHAR(100),
    total_copies INTEGER DEFAULT 1,
    available_copies INTEGER DEFAULT 1,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (library_id) REFERENCES libraries(id)
);

-- Loans 테이블
CREATE TABLE loans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    loan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Reservations 테이블
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- 인덱스 생성
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_books_library_id ON books(library_id);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_loans_user_id ON loans(user_id);
CREATE INDEX idx_loans_book_id ON loans(book_id);
CREATE INDEX idx_reservations_user_id ON reservations(user_id);
CREATE INDEX idx_reservations_book_id ON reservations(book_id);
```

## 3. 샘플 데이터 삽입

```sql
-- 샘플 도서관 데이터
INSERT INTO libraries (name, address, phone, latitude, longitude, opening_hours, description) VALUES
('서울시립도서관', '서울특별시 강남구 테헤란로 123', '02-1234-5678', 37.5665, 126.9780, '09:00-18:00', '서울시 대표 도서관'),
('강남구립도서관', '서울특별시 강남구 역삼동 456', '02-2345-6789', 37.5013, 127.0396, '09:00-21:00', '강남구 지역 도서관'),
('국립중앙도서관', '서울특별시 서초구 반포대로 201', '02-3456-7890', 37.4981, 127.0276, '09:00-18:00', '국립 도서관');

-- 샘플 도서 데이터
INSERT INTO books (library_id, title, author, isbn, publisher, publication_year, category, total_copies, available_copies) VALUES
(1, '스프링 부트 실전 활용', '김철수', '978-1234567890', '한빛미디어', 2023, '컴퓨터/IT', 3, 3),
(1, '코틀린 프로그래밍', '이영희', '978-1234567891', '길벗', 2023, '컴퓨터/IT', 2, 2),
(2, '데이터베이스 설계', '박민수', '978-1234567892', '정보문화사', 2022, '컴퓨터/IT', 1, 1),
(2, '웹 개발의 정석', '최지영', '978-1234567893', '위키북스', 2023, '컴퓨터/IT', 2, 2),
(3, '자바스크립트 완벽 가이드', '정수민', '978-1234567894', '인사이트', 2022, '컴퓨터/IT', 1, 1);

-- 샘플 사용자 데이터 (비밀번호는 BCrypt로 해시된 값)
INSERT INTO users (email, password, name, phone, address) VALUES
('user1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '김사용자', '010-1234-5678', '서울시 강남구'),
('user2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '이사용자', '010-2345-6789', '서울시 서초구');
```

## 4. 연결 테스트

애플리케이션 실행 후 다음 URL로 접속하여 데이터베이스 연결을 테스트할 수 있습니다:
- http://localhost:8080/actuator/health

## 5. 문제 해결

### PostgreSQL 서비스가 시작되지 않는 경우
```bash
# Windows 서비스에서 PostgreSQL 서비스 시작
net start postgresql-x64-15
```

### 포트 충돌이 발생하는 경우
- PostgreSQL 설정 파일에서 포트 변경
- 또는 다른 포트 사용

### 권한 문제가 발생하는 경우
```sql
-- PostgreSQL에 접속 후
ALTER USER library_user CREATEDB;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO library_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO library_user;
``` 