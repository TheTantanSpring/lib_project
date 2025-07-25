# 도서 대출 및 예약 시스템 - MVP 요구사항 정의서

## 1. 프로젝트 개요

### 1.1 프로젝트 목적
- **MVP (Minimum Viable Product)** 버전으로 핵심 기능만 구현
- 도서관 정보 검색 및 도서 관리 시스템의 기본 기능 제공
- 사용자가 쉽게 도서를 검색하고 대출/예약/반납할 수 있는 서비스

### 1.2 MVP 범위
- 도서관 정보 검색 (카카오 지도 연동)
- 도서 관리 (대출/예약/반납/검색)
- 기본적인 사용자 인증

## 2. 핵심 기능 요구사항

### 2.1 도서관 정보 검색

#### 2.1.1 도서관 목록 조회
- **기능**: 등록된 도서관 목록을 조회
- **데이터**: 도서관 이름, 주소, 연락처, 운영시간
- **표시**: 목록 형태로 간단한 정보 표시

#### 2.1.2 도서관 위치 확인 (카카오 지도)
- **기능**: 카카오 지도 API를 활용한 도서관 위치 표시
- **표시 내용**:
  - 도서관 위치 마커
  - 도서관 이름 및 주소
  - 클릭 시 상세 정보 팝업
- **지도 기능**:
  - 줌 인/아웃
  - 지도 이동
  - 현재 위치 기반 도서관 검색 (선택사항)

#### 2.1.3 도서관 상세 정보
- **기능**: 특정 도서관의 상세 정보 조회
- **표시 내용**:
  - 도서관 기본 정보 (이름, 주소, 연락처)
  - 운영시간
  - 시설 정보 (열람실, 세미나실 등)

### 2.2 도서 관리

#### 2.2.1 도서 검색
- **기능**: 도서 검색 및 필터링
- **검색 조건**:
  - 도서명 검색
  - 저자명 검색
  - ISBN 검색
  - 카테고리별 필터링
- **검색 결과**:
  - 도서 목록 표시
  - 페이징 처리 (한 페이지당 10-20권)
  - 정렬 기능 (도서명, 저자, 출판일 순)

#### 2.2.2 도서 상세 정보
- **기능**: 선택한 도서의 상세 정보 조회
- **표시 내용**:
  - 도서 기본 정보 (제목, 저자, 출판사, ISBN, 출판일)
  - 도서 소개
  - 대출 상태 (대출 가능/대출 중/예약 중)
  - 소속 도서관 정보
  - 예약 대기 인원 (대출 중인 경우)

#### 2.2.3 도서 대출
- **기능**: 도서 대출 신청
- **대출 조건**:
  - 로그인한 사용자만 대출 가능
  - 대출 가능한 도서만 대출 신청 가능
  - 최대 5권까지 동시 대출 가능
- **대출 정보**:
  - 대출일: 신청한 날짜
  - 반납 예정일: 대출일 + 14일
  - 대출 상태: "대출 중"

#### 2.2.4 도서 예약
- **기능**: 대출 중인 도서 예약
- **예약 조건**:
  - 로그인한 사용자만 예약 가능
  - 대출 중인 도서만 예약 가능
  - 이미 예약한 도서는 중복 예약 불가
- **예약 정보**:
  - 예약일: 신청한 날짜
  - 예약 만료일: 예약일 + 3일 (도서 반납 후 3일 내 수령)
  - 예약 상태: "예약 대기"

#### 2.2.5 도서 반납
- **기능**: 대출 중인 도서 반납
- **반납 처리**:
  - 반납일 기록
  - 도서 상태를 "대출 가능"으로 변경
  - 예약자가 있는 경우 알림 처리
- **연체 확인**:
  - 반납 예정일 초과 시 연체료 계산
  - 연체 정보 표시

#### 2.2.6 대출/예약 이력 조회
- **기능**: 사용자의 대출 및 예약 이력 조회
- **조회 내용**:
  - 현재 대출 중인 도서 목록
  - 과거 대출 이력
  - 현재 예약 중인 도서 목록
  - 반납 예정일 및 연체 정보

### 2.3 사용자 인증

#### 2.3.1 회원가입
- **기능**: 새로운 사용자 회원가입
- **필수 정보**:
  - 이메일 (아이디로 사용)
  - 비밀번호
  - 이름
  - 전화번호
- **검증**:
  - 이메일 중복 확인
  - 비밀번호 복잡도 검증

#### 2.3.2 로그인/로그아웃
- **기능**: 사용자 로그인 및 로그아웃
- **인증 방식**: 이메일/비밀번호 로그인
- **세션 관리**: JWT 토큰 기반 인증

## 3. 비기능 요구사항

### 3.1 성능 요구사항
- 페이지 로딩 시간: 3초 이내
- 검색 결과 응답 시간: 2초 이내
- 동시 사용자: 최대 100명

### 3.2 보안 요구사항
- 사용자 비밀번호 암호화 저장
- HTTPS 통신
- SQL Injection 방지
- XSS 공격 방지

### 3.3 사용성 요구사항
- 직관적인 UI/UX
- 모바일 반응형 디자인
- 접근성 고려

## 4. 기술 스택

### 4.1 Frontend
- **React.js** - 사용자 인터페이스
- **TypeScript** - 타입 안정성
- **Tailwind CSS** - 스타일링
- **Axios** - HTTP 클라이언트

### 4.2 Backend
- **Spring Boot** - 웹 프레임워크
- **Kotlin** - 프로그래밍 언어
- **Spring Data JPA** - ORM
- **PostgreSQL** - 데이터베이스

### 4.3 External APIs
- **카카오 지도 API** - 지도 및 위치 서비스

## 5. 데이터 모델 (간소화)

### 5.1 주요 엔티티
```
Users (사용자)
- id, email, password, name, phone, created_at

Libraries (도서관)
- id, name, address, phone, hours, latitude, longitude

Books (도서)
- id, title, author, isbn, publisher, category, status, library_id

Loans (대출)
- id, user_id, book_id, loan_date, due_date, return_date, status

Reservations (예약)
- id, user_id, book_id, reserve_date, expire_date, status
```

## 6. MVP 개발 우선순위

### 6.1 Phase 1 (1-2주차)
- 기본 프로젝트 설정
- 데이터베이스 설계 및 구축
- 사용자 인증 (회원가입/로그인)

### 6.2 Phase 2 (3-4주차)
- 도서 관리 기능 (검색, 상세정보, 대출/반납)
- 예약 시스템

### 6.3 Phase 3 (5-6주차)
- 도서관 정보 관리
- 카카오 지도 API 연동
- 통합 테스트 및 배포

## 7. 성공 기준

### 7.1 기능적 성공 기준
- 사용자가 도서를 검색하고 대출/예약/반납할 수 있음
- 도서관 위치를 지도에서 확인할 수 있음
- 기본적인 사용자 인증이 정상 동작함

### 7.2 비기능적 성공 기준
- 페이지 로딩 시간 3초 이내
- 검색 기능이 2초 이내에 응답
- 시스템 안정성 95% 이상 