# 도서 대출 및 예약 시스템 - MVP 시스템 설계서

## 1. 시스템 아키텍처

### 1.1 전체 아키텍처
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (React)       │◄──►│   (Spring Boot) │◄──►│   (PostgreSQL)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │
         │                       │
         ▼                       ▼
┌─────────────────┐    ┌─────────────────┐
│   Kakao Map     │    │   JWT Token     │
│     API         │    │   Management    │
└─────────────────┘    └─────────────────┘
```

### 1.2 레이어 아키텍처 (Backend)
```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   REST API  │  │   Security  │  │   Exception │         │
│  │  Controllers│  │   Filters   │  │   Handler   │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                     Business Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Services  │  │   Validators│  │   Utils     │         │
│  │             │  │             │  │             │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                     Data Access Layer                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │  Repositories│  │   JPA/Hibernate│  │   Query DSL  │         │
│  │             │  │             │  │             │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                     Database Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ PostgreSQL  │  │   Indexes   │  │   Constraints│         │
│  │   (Main DB) │  │             │  │             │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

## 2. 패키지 구조

### 2.1 Backend 패키지 구조 (도메인 중심)

**도메인 중심 설계의 장점:**
- **응집도 향상**: 관련된 기능들이 한 곳에 모여 있어 유지보수성 향상
- **결합도 감소**: 도메인 간 의존성이 명확해져 변경 영향도 최소화
- **확장성**: 새로운 도메인 추가 시 독립적으로 개발 가능
- **팀 협업**: 도메인별로 팀을 나누어 병렬 개발 가능

**Security 위치에 대한 고려사항:**
- **user 도메인 내 security**: 사용자 인증/인가는 사용자 도메인의 핵심 기능이므로 user 도메인에 포함
- **config/SecurityConfig**: Spring Security 전역 설정은 config에 유지
- **도메인별 권한**: 각 도메인에서 필요한 권한 검증은 해당 도메인 내에서 처리
```
com.library.mvp
├── LibraryMvpApplication.kt              # 메인 애플리케이션 클래스
├── config/                               # 설정 클래스들
│   ├── SecurityConfig.kt                 # Spring Security 설정
│   ├── DatabaseConfig.kt                 # 데이터베이스 설정
│   └── KakaoApiConfig.kt                 # 카카오 API 설정
├── user/                                 # 사용자 도메인
│   ├── controller/
│   │   ├── AuthController.kt             # 인증 관련 API
│   │   └── UserController.kt             # 사용자 관련 API
│   ├── service/
│   │   ├── AuthService.kt                # 인증 서비스
│   │   └── UserService.kt                # 사용자 서비스
│   ├── repository/
│   │   └── UserRepository.kt             # 사용자 리포지토리
│   ├── entity/
│   │   └── User.kt                       # 사용자 엔티티
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.kt           # 로그인 요청
│   │   │   ├── RegisterRequest.kt        # 회원가입 요청
│   │   │   └── UserUpdateRequest.kt      # 사용자 정보 수정 요청
│   │   └── response/
│   │       ├── UserResponse.kt           # 사용자 응답
│   │       └── UserStatsResponse.kt      # 사용자 통계 응답
│   └── security/                         # 사용자 도메인 보안 관련
│       ├── JwtTokenProvider.kt           # JWT 토큰 제공자
│       ├── UserDetailsServiceImpl.kt     # 사용자 상세 서비스
│       └── SecurityUtil.kt               # 보안 유틸리티
├── book/                                 # 도서 도메인
│   ├── controller/
│   │   └── BookController.kt             # 도서 관련 API
│   ├── service/
│   │   └── BookService.kt                # 도서 서비스
│   ├── repository/
│   │   └── BookRepository.kt             # 도서 리포지토리
│   ├── entity/
│   │   └── Book.kt                       # 도서 엔티티
│   └── dto/
│       ├── request/
│       │   └── BookSearchRequest.kt      # 도서 검색 요청
│       └── response/
│           └── BookResponse.kt           # 도서 응답
├── loan/                                 # 대출 도메인
│   ├── controller/
│   │   └── LoanController.kt             # 대출 관련 API
│   ├── service/
│   │   └── LoanService.kt                # 대출 서비스
│   ├── repository/
│   │   └── LoanRepository.kt             # 대출 리포지토리
│   ├── entity/
│   │   └── Loan.kt                       # 대출 엔티티
│   └── dto/
│       ├── request/
│       │   └── LoanRequest.kt            # 대출 요청
│       └── response/
│           └── LoanResponse.kt           # 대출 응답
├── reservation/                          # 예약 도메인
│   ├── controller/
│   │   └── ReservationController.kt      # 예약 관련 API
│   ├── service/
│   │   └── ReservationService.kt         # 예약 서비스
│   ├── repository/
│   │   └── ReservationRepository.kt      # 예약 리포지토리
│   ├── entity/
│   │   └── Reservation.kt                # 예약 엔티티
│   └── dto/
│       ├── request/
│       │   └── ReservationRequest.kt     # 예약 요청
│       └── response/
│           └── ReservationResponse.kt    # 예약 응답
├── library/                              # 도서관 도메인
│   ├── controller/
│   │   └── LibraryController.kt          # 도서관 관련 API
│   ├── service/
│   │   └── LibraryService.kt             # 도서관 서비스
│   ├── repository/
│   │   └── LibraryRepository.kt          # 도서관 리포지토리
│   ├── entity/
│   │   └── Library.kt                    # 도서관 엔티티
│   └── dto/
│       └── response/
│           └── LibraryResponse.kt        # 도서관 응답
├── common/                               # 공통 모듈
│   ├── exception/                        # 예외 처리
│   │   ├── GlobalExceptionHandler.kt     # 전역 예외 처리
│   │   ├── BookNotFoundException.kt      # 도서 없음 예외
│   │   ├── LoanException.kt              # 대출 관련 예외
│   │   └── UserNotFoundException.kt      # 사용자 없음 예외
│   ├── dto/                              # 공통 DTO
│   │   └── ApiResponse.kt                # API 응답
│   └── util/                             # 유틸리티 클래스
│       ├── DateUtil.kt                   # 날짜 유틸리티
│       ├── ValidationUtil.kt             # 검증 유틸리티
│       └── PasswordUtil.kt               # 비밀번호 유틸리티
```

### 2.2 도메인별 상세 구조

#### 2.2.1 User 도메인
```
user/
├── controller/                    # 사용자 관련 API 엔드포인트
│   ├── AuthController.kt         # 로그인, 회원가입, 토큰 갱신
│   └── UserController.kt         # 프로필 조회/수정, 사용자 통계
├── service/                      # 사용자 비즈니스 로직
│   ├── AuthService.kt            # 인증 관련 서비스 (로그인, 회원가입)
│   └── UserService.kt            # 사용자 관리 서비스 (프로필, 통계)
├── repository/                   # 사용자 데이터 접근
│   └── UserRepository.kt         # 사용자 CRUD, 통계 조회
├── entity/                       # 사용자 엔티티
│   └── User.kt                   # 사용자 정보 엔티티
├── dto/                          # 사용자 관련 DTO
│   ├── request/                  # 요청 DTO
│   │   ├── LoginRequest.kt       # 로그인 요청
│   │   ├── RegisterRequest.kt    # 회원가입 요청
│   │   └── UserUpdateRequest.kt  # 프로필 수정 요청
│   └── response/                 # 응답 DTO
│       ├── UserResponse.kt       # 사용자 정보 응답
│       └── UserStatsResponse.kt  # 사용자 통계 응답
└── security/                     # 사용자 도메인 보안
    ├── JwtTokenProvider.kt       # JWT 토큰 생성/검증
    ├── UserDetailsServiceImpl.kt # Spring Security 사용자 상세 서비스
    └── SecurityUtil.kt           # 보안 관련 유틸리티
```

#### 2.2.2 Book 도메인
```
book/
├── controller/                   # 도서 관련 API 엔드포인트
│   └── BookController.kt         # 도서 검색, 상세 조회
├── service/                      # 도서 비즈니스 로직
│   └── BookService.kt            # 도서 검색, 상태 관리
├── repository/                   # 도서 데이터 접근
│   └── BookRepository.kt         # 도서 CRUD, 검색 쿼리
├── entity/                       # 도서 엔티티
│   └── Book.kt                   # 도서 정보 엔티티
└── dto/                          # 도서 관련 DTO
    ├── request/                  # 요청 DTO
    │   └── BookSearchRequest.kt  # 도서 검색 요청
    └── response/                 # 응답 DTO
        └── BookResponse.kt       # 도서 정보 응답
```

#### 2.2.3 Loan 도메인
```
loan/
├── controller/                   # 대출 관련 API 엔드포인트
│   └── LoanController.kt         # 대출 신청, 이력 조회, 반납
├── service/                      # 대출 비즈니스 로직
│   └── LoanService.kt            # 대출 처리, 연체 관리
├── repository/                   # 대출 데이터 접근
│   └── LoanRepository.kt         # 대출 CRUD, 이력 조회
├── entity/                       # 대출 엔티티
│   └── Loan.kt                   # 대출 정보 엔티티
└── dto/                          # 대출 관련 DTO
    ├── request/                  # 요청 DTO
    │   └── LoanRequest.kt        # 대출 신청 요청
    └── response/                 # 응답 DTO
        └── LoanResponse.kt       # 대출 정보 응답
```

#### 2.2.4 Reservation 도메인
```
reservation/
├── controller/                   # 예약 관련 API 엔드포인트
│   └── ReservationController.kt  # 예약 신청, 조회, 취소
├── service/                      # 예약 비즈니스 로직
│   └── ReservationService.kt     # 예약 처리, 대기열 관리
├── repository/                   # 예약 데이터 접근
│   └── ReservationRepository.kt  # 예약 CRUD, 대기열 조회
├── entity/                       # 예약 엔티티
│   └── Reservation.kt            # 예약 정보 엔티티
└── dto/                          # 예약 관련 DTO
    ├── request/                  # 요청 DTO
    │   └── ReservationRequest.kt # 예약 신청 요청
    └── response/                 # 응답 DTO
        └── ReservationResponse.kt # 예약 정보 응답
```

#### 2.2.5 Library 도메인
```
library/
├── controller/                   # 도서관 관련 API 엔드포인트
│   └── LibraryController.kt      # 도서관 목록, 상세 정보
├── service/                      # 도서관 비즈니스 로직
│   └── LibraryService.kt         # 도서관 정보 관리
├── repository/                   # 도서관 데이터 접근
│   └── LibraryRepository.kt      # 도서관 CRUD
├── entity/                       # 도서관 엔티티
│   └── Library.kt                # 도서관 정보 엔티티
└── dto/                          # 도서관 관련 DTO
    └── response/                 # 응답 DTO
        └── LibraryResponse.kt    # 도서관 정보 응답
```

### 2.2 Frontend 패키지 구조
```
src/
├── components/                           # 재사용 가능한 컴포넌트
│   ├── common/                           # 공통 컴포넌트
│   │   ├── Header.tsx                    # 헤더 컴포넌트
│   │   ├── Footer.tsx                    # 푸터 컴포넌트
│   │   ├── Navigation.tsx                # 네비게이션 컴포넌트
│   │   └── Loading.tsx                   # 로딩 컴포넌트
│   ├── book/                             # 도서 관련 컴포넌트
│   │   ├── BookList.tsx                  # 도서 목록 컴포넌트
│   │   ├── BookDetail.tsx                # 도서 상세 컴포넌트
│   │   └── BookSearch.tsx                # 도서 검색 컴포넌트
│   ├── loan/                             # 대출 관련 컴포넌트
│   │   ├── LoanHistory.tsx               # 대출 이력 컴포넌트
│   │   └── LoanForm.tsx                  # 대출 신청 컴포넌트
│   └── library/                          # 도서관 관련 컴포넌트
│       ├── LibraryList.tsx               # 도서관 목록 컴포넌트
│       └── LibraryMap.tsx                # 도서관 지도 컴포넌트
├── pages/                                # 페이지 컴포넌트
│   ├── Home.tsx                          # 홈 페이지
│   ├── Login.tsx                         # 로그인 페이지
│   ├── Register.tsx                      # 회원가입 페이지
│   ├── BookSearch.tsx                    # 도서 검색 페이지
│   ├── MyPage.tsx                        # 마이페이지
│   └── LibraryMap.tsx                    # 도서관 지도 페이지
├── services/                             # API 서비스
│   ├── api.ts                            # API 설정
│   ├── authService.ts                    # 인증 서비스
│   ├── bookService.ts                    # 도서 서비스
│   ├── loanService.ts                    # 대출 서비스
│   └── libraryService.ts                 # 도서관 서비스
├── hooks/                                # 커스텀 훅
│   ├── useAuth.ts                        # 인증 훅
│   ├── useBooks.ts                       # 도서 훅
│   └── useLoans.ts                       # 대출 훅
├── context/                              # React Context
│   └── AuthContext.tsx                   # 인증 컨텍스트
├── types/                                # TypeScript 타입 정의
│   ├── book.ts                           # 도서 타입
│   ├── user.ts                           # 사용자 타입
│   ├── loan.ts                           # 대출 타입
│   └── library.ts                        # 도서관 타입
└── utils/                                # 유틸리티 함수
    ├── constants.ts                      # 상수 정의
    ├── helpers.ts                        # 헬퍼 함수
    └── validation.ts                     # 검증 함수
```

## 3. 보안 설계

### 3.1 인증 및 권한 관리
- **JWT 토큰 기반 인증**
  - Access Token: 30분 유효
  - Refresh Token: 7일 유효
  - 토큰 갱신 자동화

- **Spring Security 설정**
  - CORS 설정
  - CSRF 보호
  - XSS 방지
  - SQL Injection 방지

### 3.2 데이터 보안
- **비밀번호 암호화**: BCrypt 사용
- **HTTPS 통신**: SSL/TLS 인증서 적용

## 4. 성능 최적화

### 4.1 데이터베이스 최적화
- **인덱스 설계**
  - 도서 검색을 위한 복합 인덱스
  - 대출 이력 조회를 위한 인덱스
  - 사용자 이메일 인덱스

- **쿼리 최적화**
  - N+1 문제 해결을 위한 Fetch Join 사용
  - 페이징 처리 최적화

### 4.2 프론트엔드 최적화
- **React 최적화**
  - React.memo 사용
  - useMemo, useCallback 활용
  - 코드 스플리팅 적용

## 5. 배포 아키텍처

### 5.1 개발 환경
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (localhost:3000)│◄──►│   (localhost:8080)│◄──►│   (localhost:5432)│
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 5.2 운영 환경 (향후 확장)
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (CDN/Static)  │◄──►│   (Load Balancer)│◄──►│   (PostgreSQL)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
``` 