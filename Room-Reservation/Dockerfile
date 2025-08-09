# Room Reservation Server Dockerfile
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 파일들 복사 (레이어 캐싱 최적화)
COPY gradlew ./
COPY gradle gradle/
COPY build.gradle settings.gradle ./

# Gradle 의존성 다운로드
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src/

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon

# 실행
EXPOSE 8080
CMD ["java", "-jar", "build/libs/system-0.0.1-SNAPSHOT.jar"]
