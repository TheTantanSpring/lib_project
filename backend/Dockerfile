# Stage 1: Gradle을 사용하여 애플리케이션 빌드
FROM gradle:8.7-jdk17 AS build
WORKDIR /home/gradle/src

# 빌드 컨텍스트를 복사합니다.
COPY --chown=gradle:gradle . .

# Gradle을 사용하여 실행 가능한 JAR 파일을 빌드합니다.
# --no-daemon 옵션은 CI/CD 환경에서 권장됩니다.
RUN gradle bootJar --no-daemon

# Stage 2: 가벼운 Java 런타임 환경에서 최종 이미지 생성
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일을 복사합니다.
# build/libs/ 경로 아래의 모든 .jar 파일을 app.jar로 복사합니다.
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# 애플리케이션이 실행될 포트를 노출합니다.
EXPOSE 8080

# 애플리케이션 실행 명령어
# spring.profiles.active=prod를 설정하여 운영 환경 프로필을 활성화합니다.
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"] 