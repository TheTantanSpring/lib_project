# 도서 대출 및 예약 시스템 - MVP API 명세서

## 1. API 개요

### 1.1 기본 정보
- **Base URL**: `http://localhost:8080/api`
- **API 버전**: v1
- **Content-Type**: `application/json`
- **인증 방식**: JWT Bearer Token

### 1.2 응답 형식
```json
{
  "success": true,
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {},
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 1.3 에러 응답 형식
```json
{
  "success": false,
  "message": "에러 메시지",
  "error": {
    "code": "ERROR_CODE",
    "details": "상세 에러 정보"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## 2. API 엔드포인트 요약

### 2.1 인증 관련
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/login` - 로그인

### 2.2 도서관 관련
- `GET /api/libraries` - 도서관 목록 조회
- `GET /api/libraries/{id}` - 도서관 상세 정보

### 2.3 도서 관련
- `GET /api/books` - 도서 목록 조회
- `GET /api/books/{id}` - 도서 상세 조회

### 2.4 대출 관련
- `POST /api/loans` - 도서 대출
- `GET /api/loans` - 대출 이력 조회
- `PUT /api/loans/{id}/return` - 도서 반납

### 2.5 예약 관련
- `POST /api/reservations` - 도서 예약
- `GET /api/reservations` - 예약 목록 조회
- `DELETE /api/reservations/{id}` - 예약 취소

### 2.6 사용자 관련
- `GET /api/users/profile` - 프로필 조회
- `PUT /api/users/profile` - 프로필 수정

## 3. 인증 API

### 3.1 회원가입
```http
POST /api/auth/register
```

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동",
  "phone": "010-1234-5678"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동"
  }
}
```

### 3.2 로그인
```http
POST /api/auth/login
```

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "로그인이 완료되었습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "user@example.com",
      "name": "홍길동"
    }
  }
}
```

## 4. 도서관 API

### 4.1 도서관 목록 조회
```http
GET /api/libraries
```

**Response (200):**
```json
{
  "success": true,
  "message": "도서관 목록 조회 성공",
  "data": [
    {
      "id": 1,
      "name": "서울시립도서관",
      "address": "서울특별시 강남구 테헤란로 123",
      "phone": "02-1234-5678",
      "latitude": 37.5665,
      "longitude": 126.978,
      "openingHours": "09:00-18:00",
      "description": "서울시 대표 도서관",
      "createdAt": "2025-07-03T07:28:19.567098",
      "updatedAt": "2025-07-03T07:28:19.567098"
    }, ...]
}
```

### 4.2 도서관 상세 정보
```http
GET /api/libraries/{id}
```

**Response (200):**
```json
{
  "success": true,
  "message": "도서관 정보 조회 성공",
  "data": {
    "id": 2,
    "name": "서울시립도서관",
    "address": "서울특별시 강남구 테헤란로 123",
    "phone": "02-1234-5678",
    "latitude": 37.5665,
    "longitude": 126.978,
    "openingHours": "09:00-18:00",
    "description": "서울시 대표 도서관",
    "createdAt": "2025-07-03T02:54:09.530852",
    "updatedAt": "2025-07-03T02:54:09.530852"
  },
  "timestamp": 1751511429630
}
```

## 5. 도서 API

### 5.1 도서 목록 조회
```http
GET /api/books?page=0&size=10&category=소설&search=해리포터
```

**Query Parameters:**
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 10, 최대: 20)
- `category`: 카테고리 필터
- `search`: 검색어 (제목, 저자, ISBN)
- `libraryId`: 도서관 ID 필터

**Response (200):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "해리포터와 마법사의 돌",
        "author": "J.K. 롤링",
        "isbn": "9788983921985",
        "publisher": "문학수첩",
        "category": "소설",
        "status": "AVAILABLE",
        "libraryId": 1,
        "libraryName": "중앙도서관"
      }
    ],
    "totalElements": 100,
    "totalPages": 10,
    "currentPage": 0,
    "size": 10
  }
}
```

### 5.2 도서 상세 조회
```http
GET /api/books/{id}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "해리포터와 마법사의 돌",
    "author": "J.K. 롤링",
    "isbn": "9788983921985",
    "publisher": "문학수첩",
    "category": "소설",
    "status": "AVAILABLE",
    "libraryId": 1,
    "libraryName": "중앙도서관",
    "reservationCount": 0
  }
}
```

## 6. 대출 API

### 6.1 대출 신청
```http
POST /api/loans
Authorization: Bearer {accessToken}
```

**Request Body:**
```json
{
  "bookId": 1
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "도서 대출이 완료되었습니다.",
  "data": {
    "id": 1,
    "bookId": 1,
    "bookTitle": "해리포터와 마법사의 돌",
    "loanDate": "2024-01-01T00:00:00Z",
    "dueDate": "2024-01-15T00:00:00Z",
    "status": "ACTIVE"
  }
}
```

### 6.2 대출 이력 조회
```http
GET /api/loans?status=ACTIVE&page=0&size=10
Authorization: Bearer {accessToken}
```

**Query Parameters:**
- `status`: 대출 상태 (ACTIVE, RETURNED, OVERDUE)
- `page`: 페이지 번호
- `size`: 페이지 크기

**Response (200):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "bookId": 1,
        "bookTitle": "해리포터와 마법사의 돌",
        "bookAuthor": "J.K. 롤링",
        "loanDate": "2024-01-01T00:00:00Z",
        "dueDate": "2024-01-15T00:00:00Z",
        "returnDate": null,
        "status": "ACTIVE",
        "overdueDays": 0
      }
    ],
    "totalElements": 3,
    "totalPages": 1,
    "currentPage": 0,
    "size": 10
  }
}
```

### 6.3 도서 반납
```http
PUT /api/loans/{id}/return
Authorization: Bearer {accessToken}
```

**Response (200):**
```json
{
  "success": true,
  "message": "도서 반납이 완료되었습니다.",
  "data": {
    "id": 1,
    "returnDate": "2024-01-10T00:00:00Z",
    "overdueFee": 0
  }
}
```

## 7. 예약 API

### 7.1 예약 신청
```http
POST /api/reservations
Authorization: Bearer {accessToken}
```

**Request Body:**
```json
{
  "bookId": 1
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "도서 예약이 완료되었습니다.",
  "data": {
    "id": 1,
    "bookId": 1,
    "bookTitle": "해리포터와 마법사의 돌",
    "reserveDate": "2024-01-01T00:00:00Z",
    "expireDate": "2024-01-04T00:00:00Z",
    "status": "WAITING"
  }
}
```

### 7.2 예약 목록 조회
```http
GET /api/reservations?status=WAITING&page=0&size=10
Authorization: Bearer {accessToken}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "bookId": 1,
        "bookTitle": "해리포터와 마법사의 돌",
        "reserveDate": "2024-01-01T00:00:00Z",
        "expireDate": "2024-01-04T00:00:00Z",
        "status": "WAITING"
      }
    ],
    "totalElements": 2,
    "totalPages": 1,
    "currentPage": 0,
    "size": 10
  }
}
```

### 7.3 예약 취소
```http
DELETE /api/reservations/{id}
Authorization: Bearer {accessToken}
```

**Response (200):**
```json
{
  "success": true,
  "message": "예약이 취소되었습니다."
}
```

## 8. 사용자 API

### 8.1 프로필 조회
```http
GET /api/users/profile
Authorization: Bearer {accessToken}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "phone": "010-1234-5678",
    "stats": {
      "totalLoans": 15,
      "currentLoans": 2,
      "totalReservations": 5,
      "activeReservations": 1
    }
  }
}
```

### 8.2 프로필 수정
```http
PUT /api/users/profile
Authorization: Bearer {accessToken}
```

**Request Body:**
```json
{
  "name": "홍길동",
  "phone": "010-1234-5678"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "프로필이 수정되었습니다.",
  "data": {
    "id": 1,
    "name": "홍길동",
    "phone": "010-1234-5678"
  }
}
```

## 9. 에러 코드

### 9.1 공통 에러 코드
- `AUTH_001`: 인증 실패
- `AUTH_002`: 토큰 만료
- `AUTH_003`: 권한 없음
- `VALID_001`: 입력값 검증 실패
- `SYS_001`: 시스템 오류

### 9.2 도메인별 에러 코드
- `BOOK_001`: 도서를 찾을 수 없음
- `BOOK_002`: 도서가 대출 중임
- `LOAN_001`: 대출 한도 초과 (최대 5권)
- `LOAN_002`: 연체 중인 도서가 있음
- `RESV_001`: 이미 예약된 도서
- `USER_001`: 사용자를 찾을 수 없음
- `LIBRARY_001`: 도서관을 찾을 수 없음 