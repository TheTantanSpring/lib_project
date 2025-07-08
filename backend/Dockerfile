# Stage 1: Gradle을 사용하여 애플리케이션 빌드
FROM gradle:8.7-jdk17 AS build
WORKDIR /home/gradle/src

# 의존성 관리를 위한 파일만 먼저 복사
COPY build.gradle.kts settings.gradle.kts ./

# 의존성을 먼저 다운로드하여 레이어 캐시 활용
RUN gradle dependencies --no-daemon

# 전체 소스 코드를 복사
COPY . .

# Gradle을 사용하여 실행 가능한 JAR 파일을 빌드
RUN gradle bootJar --no-daemon

# Stage 2: 가벼운 Java 런타임 환경에서 최종 이미지 생성
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일을 복사
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# 애플리케이션이 실행될 포트를 노출
EXPOSE 8080

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"] 