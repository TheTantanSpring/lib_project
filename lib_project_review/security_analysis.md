# 백엔드 프로젝트 보안 분석 보고서

## 목차
1. [분석 개요](#분석-개요)
2. [중요 보안 취약점](#중요-보안-취약점)
3. [보통 위험도 보안 문제](#보통-위험도-보안-문제)
4. [낮은 위험도 보안 문제](#낮은-위험도-보안-문제)
5. [권장 사항](#권장-사항)

---

## 분석 개요

**분석 대상**: 도서관 관리 시스템 백엔드 (Spring Boot + Kotlin)  
**분석 일시**: 2024년 현재  
**분석 범위**: 인증/권한, 데이터 보안, API 보안, 설정 보안, 컨테이너 보안

---

## 중요 보안 취약점

### 1. 인증 및 권한 부여 완전 미구현 🔴 **위험도: 매우 높음**

**파일**: `src/main/kotlin/com/library/common/config/SecurityConfig.kt`

**문제점**:
```kotlin
.authorizeHttpRequests { authz ->
    authz
        .requestMatchers("/api/**").permitAll()
        .requestMatchers("/error").permitAll()
        .requestMatchers("/actuator/**").permitAll()
        .anyRequest().permitAll()
}
```

- 모든 API 엔드포인트가 `permitAll()`로 설정되어 있음
- 인증 없이 모든 도서 CRUD, 대출, 예약 기능에 접근 가능
- 관리자 기능과 일반 사용자 기능이 구분되지 않음

**영향**: 데이터 무단 접근, 수정, 삭제 가능

### 2. 비밀번호 및 JWT 비밀키 하드코딩 🔴 **위험도: 매우 높음**

**파일**: `src/main/resources/application.yml`

**문제점**:
```yaml
security:
  user:
    name: admin
    password: admin123

jwt:
  secret: your-secret-key-here-change-in-production
  expiration: 86400000
```

- 관리자 계정 비밀번호가 평문으로 노출
- JWT 비밀키가 소스코드에 하드코딩됨
- 프로덕션 환경에서도 동일한 비밀키 사용 위험

**영향**: 시스템 완전 장악 가능

### 3. 사용자 비밀번호 보안 미흡 🔴 **위험도: 높음**

**파일**: `src/main/kotlin/com/library/user/entity/User.kt`

**문제점**:
```kotlin
@Column(nullable = false)
val password: String,
```

- 비밀번호 해시화 검증 로직 없음
- 비밀번호 정책 미적용
- 비밀번호 변경 시 검증 부재

**파일**: `src/main/resources/data.sql`

**문제점**:
```sql
INSERT INTO users (username, password, email, full_name, phone, created_at, updated_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@library.com', 'Admin User', '010-9999-9999', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

- 모든 사용자가 동일한 해시값 사용 (password123)
- 초기 데이터에 관리자 계정 노출

**영향**: 계정 탈취, 무단 로그인 가능

---

## 보통 위험도 보안 문제

### 4. 과도한 CORS 설정 🟡 **위험도: 보통**

**파일**: `src/main/kotlin/com/library/common/config/SecurityConfig.kt`

**문제점**:
```kotlin
configuration.allowedOrigins = listOf(
    "http://localhost:3000",
    "http://3.34.197.195:3000",
    "http://3.34.197.195:8080"
)
configuration.allowedHeaders = listOf("*")
configuration.allowCredentials = true
```

- 모든 헤더 허용 (`*`)
- 자격 증명 허용과 함께 너무 관대한 설정
- 하드코딩된 IP 주소

**영향**: CSRF 공격 위험 증가

### 5. 민감한 정보 로깅 🟡 **위험도: 보통**

**파일**: `src/main/resources/application.yml`

**문제점**:
```yaml
logging:
  level:
    com.library: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

- 너무 상세한 로깅 레벨 설정
- SQL 바인딩 파라미터까지 로깅 (`TRACE`)
- 프로덕션에서도 동일한 로깅 레벨 사용 위험

**영향**: 로그를 통한 민감한 정보 노출

### 6. 입력 검증 부족 🟡 **위험도: 보통**

**파일**: `src/main/kotlin/com/library/book/controller/BookController.kt`

**문제점**:
```kotlin
@GetMapping("/search")
fun searchBooks(
    @RequestParam(required = false) title: String?,
    @RequestParam(required = false) author: String?,
    // ... 다른 파라미터들
) {
    // 입력 검증 없이 바로 서비스 호출
}
```

- SQL 인젝션 방어 부족
- 입력 값 길이 제한 없음
- XSS 방어 검증 부재

**영향**: 인젝션 공격 가능성

### 7. 에러 정보 과도한 노출 🟡 **위험도: 보통**

**파일**: `src/main/kotlin/com/library/loan/controller/LoanController.kt`

**문제점**:
```kotlin
} catch (e: Exception) {
    ResponseEntity.internalServerError().body(
        ApiResponse.error("대출 생성 중 오류가 발생했습니다")
    )
}
```

- 내부 시스템 정보 노출 위험
- 디버깅 정보가 클라이언트에 노출될 수 있음

**영향**: 시스템 구조 노출

---

## 낮은 위험도 보안 문제

### 8. 테스트 엔드포인트 프로덕션 노출 🟢 **위험도: 낮음**

**파일**: `src/main/kotlin/com/library/common/controller/TestController.kt`

**문제점**:
```kotlin
@RestController
@RequestMapping("/api/test")
class TestController {
    @GetMapping("/health")
    fun health(): Map<String, Any> {
        return mapOf(
            "status" to "OK",
            "message" to "Library Management System is running!",
            "timestamp" to System.currentTimeMillis()
        )
    }
}
```

- 프로덕션 환경에서 테스트 엔드포인트 접근 가능
- 시스템 정보 노출

**영향**: 시스템 정보 수집 용이

### 9. 컨테이너 보안 설정 미흡 🟢 **위험도: 낮음**

**파일**: `Dockerfile`

**문제점**:
```dockerfile
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

- 비특권 사용자 설정 없음
- 보안 스캐닝 없음
- 최소 권한 원칙 미적용

**영향**: 컨테이너 탈출 위험

### 10. 데이터베이스 연결 정보 노출 🟢 **위험도: 낮음**

**파일**: `src/main/resources/application.yml`

**문제점**:
```yaml
datasource:
  url: jdbc:postgresql://localhost:5432/library_db?useUnicode=true&characterEncoding=UTF-8&stringtype=unspecified
  username: library_user
  password: library_password
```

- 개발 환경 DB 정보 하드코딩
- 연결 문자열에 민감한 정보 포함

**영향**: 개발 환경 데이터베이스 노출

---

## 권장 사항

### 즉시 수정 필요 (중요도: 높음)

1. **인증 시스템 구현**
   - JWT 토큰 기반 인증 구현
   - 역할 기반 권한 관리 (RBAC) 적용
   - 세션 관리 및 토큰 만료 처리

2. **비밀번호 보안 강화**
   - 환경 변수 또는 Key Vault 사용
   - 안전한 비밀번호 정책 적용
   - 비밀번호 해시화 검증

3. **설정 파일 보안**
   - 민감한 정보 환경 변수 분리
   - 프로덕션 환경별 설정 분리
   - 로깅 레벨 적절히 조정

### 단계별 개선사항

1. **1단계 (긴급)**
   - 인증 시스템 구현
   - 비밀키 환경 변수 분리
   - 사용자 권한 체계 구축

2. **2단계 (중요)**
   - 입력 검증 강화
   - CORS 설정 개선
   - 에러 처리 개선

3. **3단계 (권장)**
   - 보안 헤더 추가
   - 로깅 보안 강화
   - 컨테이너 보안 개선

### 보안 체크리스트

- [ ] Spring Security 설정 완료
- [ ] JWT 토큰 인증 구현
- [ ] 역할 기반 권한 관리
- [ ] 입력 검증 강화
- [ ] 비밀번호 정책 적용
- [ ] 환경 변수 분리
- [ ] 로깅 보안 설정
- [ ] CORS 설정 개선
- [ ] 테스트 엔드포인트 제거
- [ ] 컨테이너 보안 설정

---

**분석 완료 일시**: 2024년 현재  
**다음 검토 권장**: 보안 수정 사항 적용 후 재검토 