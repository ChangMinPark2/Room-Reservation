# 🏢 회의실 예약 시스템

## 📋 프로젝트 개요

사내 회의실 예약을 위한 **RESTful API 서버**입니다.
- 회의실 예약/조회/취소 기능
- 다중 결제사 통합 및 추상화
- Docker & Docker Compose 기반 컨테이너 환경
- Swagger UI를 통한 API 문서화 및 테스트

## 🛠️ 기술 스택

### Backend
- **언어**: Java 17
- **프레임워크**: Spring Boot 3.5.4
- **데이터베이스**: MySQL 8.0
- **ORM**: Spring Data JPA
- **API 문서화**: Swagger (OpenAPI 3.0)
- **빌드 도구**: Gradle

### Infrastructure
- **컨테이너**: Docker + Docker Compose
- **테스트**: JUnit 5

## 🏗️ 시스템 아키텍처

### 데이터 모델
```
User (사용자)
├── id, name, email, phoneNumber

MeetingRoom (회의실)
├── id, name, capacity, hourlyRate, isActive

Reservation (예약)
├── id, user, meetingRoom, startTime, endTime, totalAmount, status

Payment (결제)
├── id, reservation, paymentProvider, amount, status, externalPaymentId

PaymentProvider (결제사)
├── id, name, apiEndpoint, apiKey, secretKey, type, isActive
```

### 결제 시스템 추상화
- **Strategy Pattern**을 사용한 결제사별 구현체 분리
- **공통 인터페이스**: `PaymentService`
- **결제사 타입**:
  - A사: 신용카드 결제 (CardPaymentAPI)
  - B사: 간편결제 (SimplePaymentAPI)
  - C사: 가상계좌 결제 (VirtualAccountAPI)

## 🚀 실행 방법

### 1. Docker Compose로 전체 환경 실행
```bash
# 프로젝트 빌드
./gradlew build

# Docker Compose 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f
```

### 2. Swagger UI 접속
```
http://localhost:8080/docs
```

### 3. API 테스트
```bash
# 회의실 목록 조회
curl http://localhost:8080/api/meeting-rooms

# 예약 생성
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "meetingRoomId": 1,
    "startTime": "2024-01-15T10:00:00",
    "endTime": "2024-01-15T12:00:00"
  }'
```

## 📊 API 엔드포인트

### 회의실 관리
- `GET /api/meeting-rooms` - 회의실 목록 조회
- `GET /api/meeting-rooms/{id}` - 회의실 상세 조회

### 예약 관리
- `POST /api/reservations` - 예약 생성
- `GET /api/reservations` - 예약 목록 조회
- `GET /api/reservations/{id}` - 예약 상세 조회
- `PUT /api/reservations/{id}` - 예약 수정
- `DELETE /api/reservations/{id}` - 예약 취소

### 결제 관리
- `POST /api/reservations/{id}/payment` - 결제 처리
- `GET /api/payments/{paymentId}/status` - 결제 상태 조회
- `POST /api/webhooks/payments/{provider}` - 결제사별 웹훅 수신

## 🔒 비즈니스 규칙

### 예약 규칙
- ✅ **예약 시간 중복 방지** (동일 회의실)
- ✅ 시작 시간 < 종료 시간
- ✅ **정시(00분) 또는 30분 단위**로만 예약 가능
- ✅ 요청 파라미터 **유효성 검사(Validation)** 필수

### 결제 규칙
- ✅ **결제 추상화 서비스** 구현
- ✅ 각 결제사 API 스펙과 응답 데이터 구조가 상이함
- ✅ 결제 결과를 **공통 데이터 모델**로 변환하여 저장

### 동시성 제어
- ✅ **교착 상태(Deadlock)** 해결 로직
- ✅ **결제 처리 중 예약 상태 관리** (결제 대기 → 결제 완료 → 예약 확정)

## 🧪 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests ReservationServiceTest

# 통합 테스트 실행
./gradlew integrationTest
```

## 📝 개발 가이드

### 코드 구조
```
src/main/java/com/room/reservation/system/
├── api/
│   ├── controller/     # REST API 컨트롤러
│   ├── dto/           # Data Transfer Objects
│   ├── service/        # 비즈니스 로직
│   └── persistence/   # 데이터 접근 계층
├── global/            # 전역 설정, 예외 처리
└── SystemApplication.java
```

### 네이밍 컨벤션
- **클래스명**: PascalCase (예: `MeetingRoomService`)
- **메서드명**: camelCase (예: `createReservation`)
- **상수명**: UPPER_SNAKE_CASE (예: `MAX_CAPACITY`)
- **패키지명**: lowercase (예: `com.room.reservation.system`)

