# 도서 대출 및 예약 시스템 - MVP 데이터베이스 설계서

## 1. ERD (Entity Relationship Diagram)

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│    Users    │         │    Books    │         │   Libraries │
├─────────────┤         ├─────────────┤         ├─────────────┤
│ id (PK)     │         │ id (PK)     │         │ id (PK)     │
│ email       │         │ title       │         │ name        │
│ password    │         │ author      │         │ address     │
│ name        │         │ isbn        │         │ phone       │
│ phone       │         │ publisher   │         │ hours       │
│ created_at  │         │ category    │         │ latitude    │
│ updated_at  │         │ status      │         │ longitude   │
└─────────────┘         │ library_id (FK) │         │ created_at  │
         │              │ created_at  │         │ updated_at  │
         │              │ updated_at  │         └─────────────┘
         │              └─────────────┘                 ▲
         │                       ▲                       │
         │                       │                       │
         │                       │ (FK)                  │
         │                       │ library_id             │
         │                       │ references             │
         │                       │ libraries.id           │
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       │
┌─────────────┐         ┌─────────────┐                 │
│    Loans    │         │Reservations │                 │
├─────────────┤         ├─────────────┤                 │
│ id (PK)     │         │ id (PK)     │                 │
│ user_id (FK)│         │ user_id (FK)│                 │
│ book_id (FK)│         │ book_id (FK)│                 │
│ loan_date   │         │ reserve_date│                 │
│ due_date    │         │ expire_date │                 │
│ return_date │         │ status      │                 │
│ status      │         │ created_at  │                 │
│ created_at  │         │ updated_at  │                 │
│ updated_at  │         └─────────────┘                 │
└─────────────┘                 ▲                       │
         ▲                       │                       │
         │                       │                       │
         │ (FK)                  │ (FK)                  │
         │ user_id               │ user_id               │
         │ references            │ references            │
         │ users.id              │ users.id              │
         │                       │                       │
         │ (FK)                  │ (FK)                  │
         │ book_id               │ book_id               │
         │ references            │ references            │
         │ books.id              │ books.id              │
         │                       │                       │
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                                 ▼
```

**관계 설명:**
- **Users (1) ←→ (N) Loans**: 한 사용자는 여러 대출을 가질 수 있음
- **Users (1) ←→ (N) Reservations**: 한 사용자는 여러 예약을 가질 수 있음
- **Books (N) ←→ (1) Libraries**: 여러 도서가 하나의 도서관에 속함
- **Books (1) ←→ (N) Loans**: 한 도서는 여러 대출 기록을 가질 수 있음
- **Books (1) ←→ (N) Reservations**: 한 도서는 여러 예약 기록을 가질 수 있음

## 2. 테이블 상세 설계

### 2.1 Users 테이블 (사용자)
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**컬럼 설명:**
- `id`: 사용자 고유 식별자 (자동 증가)
- `email`: 이메일 주소 (로그인 ID로 사용, 유니크)
- `password`: 암호화된 비밀번호
- `name`: 사용자 이름
- `phone`: 전화번호
- `created_at`: 계정 생성일
- `updated_at`: 정보 수정일

### 2.2 Libraries 테이블 (도서관)
```sql
CREATE TABLE libraries (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20),
    hours VARCHAR(100),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**컬럼 설명:**
- `id`: 도서관 고유 식별자
- `name`: 도서관 이름
- `address`: 도서관 주소
- `phone`: 연락처
- `hours`: 운영시간
- `latitude`: 위도 (카카오 지도용)
- `longitude`: 경도 (카카오 지도용)
- `created_at`: 등록일
- `updated_at`: 수정일

### 2.3 Books 테이블 (도서)
```sql
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20),
    publisher VARCHAR(255),
    category VARCHAR(100),
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    library_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (library_id) REFERENCES libraries(id)
);
```

**컬럼 설명:**
- `id`: 도서 고유 식별자
- `title`: 도서 제목
- `author`: 저자
- `isbn`: ISBN 번호
- `publisher`: 출판사
- `category`: 카테고리 (소설, 기술서, 교양서 등)
- `status`: 도서 상태 (AVAILABLE, LOANED, RESERVED)
- `library_id`: 소속 도서관 ID (외래키)
- `created_at`: 등록일
- `updated_at`: 수정일

### 2.4 Loans 테이블 (대출)
```sql
CREATE TABLE loans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    loan_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);
```

**컬럼 설명:**
- `id`: 대출 고유 식별자
- `user_id`: 대출자 ID (외래키)
- `book_id`: 대출 도서 ID (외래키)
- `loan_date`: 대출일
- `due_date`: 반납 예정일 (대출일 + 14일)
- `return_date`: 실제 반납일 (반납 시 기록)
- `status`: 대출 상태 (ACTIVE, RETURNED, OVERDUE)
- `created_at`: 생성일
- `updated_at`: 수정일

### 2.5 Reservations 테이블 (예약)
```sql
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    reserve_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expire_date TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'WAITING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);
```

**컬럼 설명:**
- `id`: 예약 고유 식별자
- `user_id`: 예약자 ID (외래키)
- `book_id`: 예약 도서 ID (외래키)
- `reserve_date`: 예약일
- `expire_date`: 예약 만료일 (예약일 + 3일)
- `status`: 예약 상태 (WAITING, COMPLETED, EXPIRED)
- `created_at`: 생성일
- `updated_at`: 수정일

## 3. 인덱스 설계

### 3.1 성능 최적화를 위한 인덱스
```sql
-- Users 테이블 인덱스
CREATE INDEX idx_users_email ON users(email);

-- Books 테이블 인덱스
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_category ON books(category);
CREATE INDEX idx_books_status ON books(status);
CREATE INDEX idx_books_library_id ON books(library_id);
CREATE INDEX idx_books_title_author ON books(title, author);

-- Loans 테이블 인덱스
CREATE INDEX idx_loans_user_id ON loans(user_id);
CREATE INDEX idx_loans_book_id ON loans(book_id);
CREATE INDEX idx_loans_status ON loans(status);
CREATE INDEX idx_loans_due_date ON loans(due_date);
CREATE INDEX idx_loans_user_status ON loans(user_id, status);

-- Reservations 테이블 인덱스
CREATE INDEX idx_reservations_user_id ON reservations(user_id);
CREATE INDEX idx_reservations_book_id ON reservations(book_id);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_expire_date ON reservations(expire_date);
```

## 4. 제약 조건

### 4.1 데이터 무결성 제약 조건
```sql
-- Users 테이블 제약 조건
ALTER TABLE users ADD CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');
ALTER TABLE users ADD CONSTRAINT chk_phone_format CHECK (phone ~* '^[0-9-+()]+$');

-- Books 테이블 제약 조건
ALTER TABLE books ADD CONSTRAINT chk_book_status CHECK (status IN ('AVAILABLE', 'LOANED', 'RESERVED'));
ALTER TABLE books ADD CONSTRAINT chk_isbn_format CHECK (isbn ~* '^[0-9-]+$');

-- Loans 테이블 제약 조건
ALTER TABLE loans ADD CONSTRAINT chk_loan_status CHECK (status IN ('ACTIVE', 'RETURNED', 'OVERDUE'));
ALTER TABLE loans ADD CONSTRAINT chk_loan_dates CHECK (due_date > loan_date);

-- Reservations 테이블 제약 조건
ALTER TABLE reservations ADD CONSTRAINT chk_reservation_status CHECK (status IN ('WAITING', 'COMPLETED', 'EXPIRED'));
ALTER TABLE reservations ADD CONSTRAINT chk_reservation_dates CHECK (expire_date > reserve_date);
```

## 5. 샘플 데이터

### 5.1 도서관 샘플 데이터
```sql
INSERT INTO libraries (name, address, phone, hours, latitude, longitude) VALUES
('중앙도서관', '서울특별시 강남구 테헤란로 123', '02-1234-5678', '09:00-18:00', 37.5665, 126.9780),
('서초도서관', '서울특별시 서초구 서초대로 456', '02-2345-6789', '09:00-21:00', 37.5013, 127.0246),
('강남도서관', '서울특별시 강남구 강남대로 789', '02-3456-7890', '09:00-18:00', 37.5172, 127.0473);
```

### 5.2 도서 샘플 데이터
```sql
INSERT INTO books (title, author, isbn, publisher, category, library_id) VALUES
('해리포터와 마법사의 돌', 'J.K. 롤링', '9788983921985', '문학수첩', '소설', 1),
('클린 코드', '로버트 C. 마틴', '9788966262472', '인사이트', '기술서', 1),
('1984', '조지 오웰', '9788932917245', '민음사', '소설', 2),
('스프링 부트 실전 활용', '김영한', '9788966262915', '인사이트', '기술서', 3);
```

## 6. 뷰 및 함수 (선택사항)

### 6.1 대출 현황 뷰
```sql
CREATE VIEW loan_status_view AS
SELECT 
    l.id as loan_id,
    u.name as user_name,
    b.title as book_title,
    b.author as book_author,
    l.loan_date,
    l.due_date,
    l.status,
    CASE 
        WHEN l.due_date < CURRENT_DATE AND l.status = 'ACTIVE' 
        THEN CURRENT_DATE - l.due_date 
        ELSE 0 
    END as overdue_days
FROM loans l
JOIN users u ON l.user_id = u.id
JOIN books b ON l.book_id = b.id;
```

### 6.2 도서 대출 가능 여부 함수
```sql
CREATE OR REPLACE FUNCTION is_book_available(book_id BIGINT)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN NOT EXISTS (
        SELECT 1 FROM loans 
        WHERE book_id = $1 AND status = 'ACTIVE'
    );
END;
$$ LANGUAGE plpgsql;
``` 