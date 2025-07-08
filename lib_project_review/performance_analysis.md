# 도서관 관리 시스템 성능 분석 보고서

## 1. 분석 개요

### 분석 대상
- **프로젝트**: Spring Boot + Kotlin 기반 도서관 관리 시스템
- **아키텍처**: 계층형 아키텍처 (Controller → Service → Repository → Entity)
- **데이터베이스**: PostgreSQL
- **분석 일자**: 2024년

### 분석 범위
- 데이터베이스 성능 및 쿼리 최적화
- 메모리 사용량 및 가비지 컬렉션
- 캐싱 전략 및 적용 현황
- 동시성 제어 및 트랜잭션 관리
- N+1 문제 및 지연 로딩 최적화
- 페이징 및 대용량 데이터 처리

---

## 2. 성능 이슈 분석

### 2.1 매우 높음 위험도 이슈

#### **1. N+1 문제 발생**
- **위치**: Service 계층의 목록 조회 메서드들
- **상세 내용**:
  ```kotlin
  // ReservationService.getAllReservations()
  val reservationResponses = reservations.map { reservation ->
      // 각 reservation마다 개별 쿼리 실행
      ReservationResponseDto.from(reservation, queuePosition)
  }
  
  // DTO 변환 시 지연 로딩 발생
  userName = reservation.user.fullName,     // 추가 쿼리
  bookTitle = reservation.book.title,       // 추가 쿼리
  libraryName = reservation.book.library.name // 추가 쿼리
  ```
- **영향**: 100개의 예약이 있을 경우 1+100+100+100 = 301개의 쿼리 실행
- **해결책**: 
  - Fetch Join 사용: `@Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.book JOIN FETCH r.book.library")`
  - Entity Graph 활용: `@EntityGraph` 애노테이션 적용

#### **2. 비효율적인 통계 계산**
- **위치**: `LoanService.getAllLoans()`, `ReservationService.getAllReservations()`
- **상세 내용**:
  ```kotlin
  // 전체 데이터를 메모리로 로드 후 계산
  val loans = loanRepository.findAll()
  return LoanListResponseDto(
      totalCount = loans.size,
      activeCount = loans.count { it.status == LoanStatus.ACTIVE },
      overdueCount = loans.count { it.status == LoanStatus.ACTIVE && it.dueDate.isBefore(LocalDateTime.now()) }
  )
  ```
- **영향**: 대용량 데이터 시 메모리 사용량 급증 및 응답 지연
- **해결책**: 
  - 데이터베이스 레벨 집계 쿼리 사용
  - 별도 통계 테이블 또는 캐싱 적용

### 2.2 높음 위험도 이슈

#### **3. 페이징 처리 부재**
- **위치**: 대부분의 목록 조회 API
- **상세 내용**:
  - BookRepository에 페이징 지원 메서드 존재하지만 Service에서 미활용
  - 모든 데이터를 한 번에 조회하여 메모리 부하 발생
- **영향**: 데이터 증가 시 응답 시간 급격히 증가
- **해결책**:
  ```kotlin
  @Transactional(readOnly = true)
  fun getAllBooks(pageable: Pageable): Page<BookResponseDto> {
      return bookRepository.findAll(pageable)
          .map { BookResponseDto.from(it) }
  }
  ```

#### **4. 동시성 제어 부족**
- **위치**: 도서 대출/반납 로직
- **상세 내용**:
  ```kotlin
  // LoanService.createLoan() - Race Condition 가능
  if (book.availableCopies <= 0) {
      throw IllegalStateException("대출 가능한 도서가 없습니다")
  }
  // 다른 사용자가 동시에 대출할 경우 문제 발생 가능
  updateBookAvailableCopies(book.id, -1)
  ```
- **영향**: 동시 접근 시 데이터 일관성 문제
- **해결책**:
  - 낙관적 잠금: `@Version` 필드 추가
  - 비관적 잠금: `@Lock(LockModeType.PESSIMISTIC_WRITE)` 사용

### 2.3 보통 위험도 이슈

#### **5. 캐싱 전략 부재**
- **위치**: 통계성 데이터 및 자주 조회되는 정보
- **상세 내용**:
  - 카테고리 통계, 도서관 정보 등 변경 빈도가 낮은 데이터도 매번 DB 조회
  - 주석에만 "캐싱으로 확장 가능"이라고 언급
- **영향**: 불필요한 DB 부하 및 응답 지연
- **해결책**:
  ```kotlin
  @Cacheable(value = "categoryStats", unless = "#result.isEmpty()")
  fun getCategoryStatistics(): List<CategoryStatsDto>
  
  @CacheEvict(value = "categoryStats", allEntries = true)
  fun createBook(request: BookCreateRequestDto): BookResponseDto
  ```

#### **6. 트랜잭션 범위 과다**
- **위치**: Service 클래스 레벨 `@Transactional` 적용
- **상세 내용**:
  ```kotlin
  @Service
  @Transactional  // 모든 메서드가 쓰기 트랜잭션으로 실행
  class BookService {
      @Transactional(readOnly = true) // 명시적으로 읽기 전용 설정 필요
      fun getAllBooks(): List<BookResponseDto>
  }
  ```
- **영향**: 불필요한 트랜잭션 오버헤드
- **해결책**: 메서드 레벨에서 필요한 경우에만 트랜잭션 적용

#### **7. 데이터베이스 연결 풀 최적화 부족**
- **위치**: `application.yml` 설정
- **상세 내용**: HikariCP 기본 설정 사용
- **영향**: 고부하 시 연결 부족 가능
- **해결책**:
  ```yaml
  spring:
    datasource:
      hikari:
        maximum-pool-size: 20
        minimum-idle: 5
        connection-timeout: 30000
        idle-timeout: 600000
        max-lifetime: 1800000
  ```

### 2.4 낮음 위험도 이슈

#### **8. 인덱스 최적화 부족**
- **위치**: 데이터베이스 스키마
- **상세 내용**: 기본 인덱스만 설정, 복합 인덱스 부족
- **영향**: 복잡한 검색 쿼리 성능 저하
- **해결책**:
  ```sql
  -- 복합 검색용 인덱스
  CREATE INDEX idx_books_library_status ON books(library_id, status);
  CREATE INDEX idx_books_category_status ON books(category, status);
  CREATE INDEX idx_loans_user_status ON loans(user_id, status);
  ```

#### **9. 쿼리 최적화 기회**
- **위치**: Repository의 동적 검색 쿼리
- **상세 내용**: 비효율적인 `LIKE` 연산자 과다 사용
- **영향**: 대용량 데이터에서 검색 성능 저하
- **해결책**: Full-Text Search 적용 고려

---

## 3. 성능 메트릭 예상 수치

### 3.1 현재 성능 추정

| 메트릭 | 현재 수치 | 목표 수치 |
|--------|-----------|-----------|
| 목록 조회 응답시간 (1000건) | 2-5초 | 200-500ms |
| N+1 쿼리 수 (100건 조회시) | 300+ 쿼리 | 1-3 쿼리 |
| 메모리 사용량 | 높음 | 중간 |
| 동시 사용자 지원 | 10-20명 | 100-200명 |
| 데이터베이스 연결 | 기본 설정 | 최적화됨 |

### 3.2 병목 지점 분석

1. **데이터베이스 I/O**: 가장 큰 병목
2. **메모리 사용량**: N+1 문제로 인한 과도한 객체 생성
3. **트랜잭션 처리**: 불필요한 쓰기 트랜잭션
4. **동시성**: 락 메커니즘 부재

---

## 4. 최적화 권장사항

### 4.1 단기 개선 과제 (1-2주)

1. **N+1 문제 해결**
   - Fetch Join 적용
   - DTO Projection 사용

2. **페이징 처리 도입**
   - Controller에서 Pageable 파라미터 추가
   - Service 메서드 반환 타입을 Page<T>로 변경

3. **트랜잭션 최적화**
   - 클래스 레벨 @Transactional 제거
   - 메서드별 적절한 트랜잭션 설정

### 4.2 중기 개선 과제 (1-2개월)

1. **캐싱 도입**
   - Spring Cache 적용
   - Redis 연동 고려

2. **동시성 제어**
   - 낙관적 잠금 구현
   - 동시성 테스트 코드 작성

3. **데이터베이스 최적화**
   - 복합 인덱스 추가
   - 연결 풀 튜닝

### 4.3 장기 개선 과제 (3-6개월)

1. **아키텍처 개선**
   - CQRS 패턴 적용 고려
   - 읽기 전용 DB 분리

2. **모니터링 체계 구축**
   - APM 도구 도입
   - 성능 메트릭 수집

3. **비동기 처리**
   - 통계 계산 비동기화
   - 이벤트 기반 아키텍처 도입

---

## 5. 모니터링 계획

### 5.1 성능 지표 모니터링

```kotlin
// 성능 측정을 위한 AOP 적용
@Around("execution(* com.library..*Service.*(..))")
fun measureExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
    val startTime = System.currentTimeMillis()
    val result = joinPoint.proceed()
    val endTime = System.currentTimeMillis()
    
    logger.info("Method ${joinPoint.signature.name} executed in ${endTime - startTime}ms")
    return result
}
```

### 5.2 주요 모니터링 항목

1. **응답 시간**
   - API별 평균 응답 시간
   - 95th percentile 응답 시간

2. **데이터베이스 성능**
   - 슬로우 쿼리 로그
   - 연결 풀 사용률

3. **메모리 사용량**
   - JVM 힙 메모리 사용률
   - GC 발생 빈도

4. **동시성**
   - 동시 사용자 수
   - 트랜잭션 대기 시간

---

## 6. 결론

### 6.1 현재 상태 평가

**성능 점수: 4/10** (상당한 개선 필요)

- **장점**: 기본적인 기능 구현 완료, 확장 가능한 구조
- **단점**: N+1 문제, 페이징 부재, 캐싱 미적용, 동시성 제어 부족

### 6.2 개선 후 예상 효과

1. **N+1 문제 해결**: 쿼리 수 90% 감소
2. **페이징 도입**: 메모리 사용량 80% 감소
3. **캐싱 적용**: 응답 시간 60% 단축
4. **동시성 제어**: 데이터 일관성 보장

### 6.3 최종 권장사항

1. **우선순위 1**: N+1 문제 해결 및 페이징 도입
2. **우선순위 2**: 캐싱 전략 수립 및 적용
3. **우선순위 3**: 동시성 제어 메커니즘 구현
4. **우선순위 4**: 모니터링 체계 구축

성능 최적화는 단계별로 진행하되, 각 단계마다 성능 측정을 통해 개선 효과를 검증하는 것이 중요합니다. 