# 📚 도서관 관리 시스템 (Library Management System)

> 도서관 위치 검색, 도서 대출/예약 관리를 위한 웹 애플리케이션

## 🌐 배포 주소

| 서비스 | URL | 상태 | 비고 |
|--------|-----|------|------|
| **프론트엔드** | http://3.34.197.195:3000 | ✅ 운영 중 | [화면 구성](https://github.com/TheTantanSpring/lib_project/wiki/%F0%9F%97%BA%EF%B8%8F%ED%99%94%EB%A9%B4-%EA%B5%AC%EC%84%B1) |
| **백엔드 API** | http://3.34.197.195:8080 | ✅ 운영 중 | |
| **데이터베이스 관리** | http://3.34.197.195:8081 | ✅ 운영 중 | |

## 🚀 주요 기능

- ✅ 도서관 목록 조회 및 검색
- ✅ 도서관 상세 정보 확인
- ✅ 카카오 지도를 통한 위치 확인
- ✅ 반응형 디자인 (모바일 지원)
- ✅ 페이지네이션
- ✅ 에러 처리 및 로딩 상태

### 🏢 도서관 관리
- 도서관 위치 검색 (카카오맵 연동)
- 도서관 상세 정보 조회
- 운영시간 및 연락처 확인

### 📖 도서 관리 (화면 구현 예정)
- 도서 검색 및 조회
- 도서 재고 관리
- 카테고리별 도서 분류

### 💳 대출 시스템 (화면 구현 예정)
- 온라인 도서 대출
- 대출 현황 조회
- 대출 연장 및 반납
- 연체 관리

### 📅 예약 시스템 (화면 구현 예정)
- 도서 예약 및 대기열 관리
- 예약 취소 및 상태 조회
- 예약 만료 자동 처리


## 📱 화면 구성

### 🏠 메인 페이지 (`/`)
- 서비스 소개
- 주요 기능 안내 카드
- 도서관 찾기 버튼 : **클릭 가능**

### 📍 도서관 목록 (`/libraries`)
- 도서관 검색 기능
- 카카오맵 위치 표시
- 도서관 카드 목록 (페이지네이션)
- 필터링 및 정렬

### 🏛 도서관 상세 (`/libraries/[id]`) - **클릭 가능**
- 도서관 상세 정보
- 위치 지도 표시 (배포 환경에서 오류 발생 수정 예정!)
- 연락처 및 운영시간

## 🔌 API 문서 (실제 구현 완료, 추후 swagger 추가 예정)

### 🏢 도서관 API (`/api/libraries`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/libraries` | 도서관 목록 조회 |
| `GET` | `/api/libraries/{id}` | 도서관 상세 조회 |
| `GET` | `/api/libraries/search` | 도서관 검색 |

### 📚 도서 API (`/api/books`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/books` | 도서 목록 조회 |
| `GET` | `/api/books/{id}` | 도서 상세 조회 |
| `POST` | `/api/books` | 도서 등록 |
| `PUT` | `/api/books/{id}` | 도서 정보 수정 |
| `DELETE` | `/api/books/{id}` | 도서 삭제 |
| `POST` | `/api/books/{id}/borrow` | 도서 대여 |
| `POST` | `/api/books/{id}/return` | 도서 반납 |

### 💳 대출 API (`/api/loans`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/loans` | 전체 대출 목록 조회 |
| `GET` | `/api/loans/{id}` | 대출 상세 조회 |
| `POST` | `/api/loans` | 대출 생성 |

### 📅 예약 API (`/api/reservations`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/reservations` | 전체 예약 목록 조회 |
| `GET` | `/api/reservations/{id}` | 예약 상세 조회 |
| `POST` | `/api/reservations` | 예약 생성 |

 
