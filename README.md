# 회의실 예약 시스템 (Room Reservation System)

## 📋 프로젝트 개요

회의실 예약 및 결제 처리를 위한 Spring Boot 기반 REST API 서버입니다.  
**예약과 결제 프로세스에 중점**을 두어 설계되었으며, 회의실 생성, 회원가입 등의 기능은 `data.sql`을 통해 초기 데이터로 대체했습니다.

### 주요 기능
- 🏢 회의실 예약 생성/조회/삭제
- 💳 다중 결제 수단 지원 (카드, 가상계좌, 간편결제)
- 🔄 결제 상태 실시간 웹훅 처리
- 📊 예약 현황 조회 및 중복 예약 방지
- 🎯 전략 패턴과 팩토리 패턴을 활용한 결제 처리

---

## 🛠 기술 스택 및 버전

| 분류 | 기술 | 버전 |
|------|------|------|
| **Backend** | Spring Boot | 3.2.0 |
| **Language** | Java | 17 |
| **Database** | MySQL | 8.0 |
| **ORM** | Spring Data JPA | 3.2.0 |
| **Build Tool** | Gradle | 8.5 |
| **Documentation** | Swagger (OpenAPI 3) | 2.2.0 |
| **Container** | Docker & Docker Compose | Latest |
| **Payment Mock** | Spring Boot | 3.2.0 |

### 아키텍처 패턴
- **전략 패턴**: 결제 수단별 처리 로직 분리
- **Repository 패턴**: 데이터 접근 계층 분리
- **DTO 패턴**: 데이터 전송 객체 활용
- **Exception Handler**: 글로벌 예외 처리

---

## 🚀 실행 방법

> **⚠️ 주의사항**: 실행 전 반드시 **Docker Desktop이 실행되어 있어야 합니다!**

### 1. 프로젝트 클론
```bash
git clone https://github.com/ChangMinPark2/Room-Reservation.git
```

### 2. 프로젝트 이동
```bash
cd Room-Reservation-Server
```

### 3. Docker Compose 실행
```bash
docker-compose up --build
```

> **💡 Tip**: 위 명령어를 클릭하면 바로 복사할 수 있습니다!

### 4. 서비스 확인
- **메인 서버**: http://localhost:8080
- **Mock 결제 서버**: http://localhost:8081
- **MySQL**: localhost:3306

### 5. 초기 데이터
애플리케이션 실행 시 `data.sql`에 의해 다음 데이터가 자동으로 생성됩니다:

#### 👥 회원 정보 (3명)
- **홍길동** (010-1234-5678)
- **김길동** (010-2345-6789)  
- **박길동** (010-3456-7890)

#### 🏢 회의실 정보 (5개)
- **A회의실** (수용인원: 10명, 시간당 요금: 25,000원)
- **B회의실** (수용인원: 20명, 시간당 요금: 40,000원)
- **C회의실** (수용인원: 15명, 시간당 요금: 30,000원)
- **대회의실** (수용인원: 50명, 시간당 요금: 75,000원)
- **소회의실** (수용인원: 5명, 시간당 요금: 15,000원)

---

## 📖 Swagger UI 접속 방법

### 메인 서버 API 문서
```
http://localhost:8080/swagger-ui/index.html
```

> **⚠️ 주의사항**: Swagger UI에서 가끔 POST(생성) 요청 시 404 에러가 발생할 수 있습니다. 이는 Swagger UI의 알려진 문제점이며, 실제 API는 정상 동작합니다. GET(조회) 요청은 문제없이 작동합니다.

> **💡 설계 의도**: 예약 조회 요청이 POST로 설계된 이유는, 원래 회원 기능을 구현했다면 헤더 정보로 사용자를 판별했겠지만, **핵심 비즈니스 로직에만 중점**을 두기 위해 **이름과 핸드폰번호로만 회원을 판별**하도록 설계했습니다.

> **⏰ 예약 시간 검증**: 예약 시작시간은 **현재 시간보다 이후**여야 합니다. 예를 들어 현재 17:07분이라면, 17:30부터 예약이 가능합니다. 이는 실제 운영 환경을 고려한 비즈니스 로직입니다.

### 주요 API 엔드포인트

#### 회의실 관련
- `GET /api/meeting-rooms` - 회의실 목록 조회
- `GET /api/meeting-rooms/{id}` - 회의실 상세 조회

#### 예약 관련
- `POST /api/reservations` - 예약 생성
- `GET /api/reservations` - 예약 목록 조회
- `GET /api/reservations/{id}` - 예약 상세 조회
- `DELETE /api/reservations/{id}` - 예약 삭제

#### 결제 관련
- `POST /api/payments` - 결제 요청
- `GET /api/payments/{id}/status` - 결제 상태 조회
- `POST /api/webhooks/payment` - 결제 웹훅 수신

### API 테스트 순서
1. **회의실 조회** → 사용 가능한 회의실 확인
2. **예약 생성** → 원하는 시간대에 예약
3. **예약 조회** (예약 대기 상태)
4. **결제 요청** → 예약에 대한 결제 진행
5. **결제 상태 확인** → 결제 완료 여부 확인
6. **예약 조회** (예약 확정 상태)
---

## 🧪 테스트 실행 방법

### 1. 메인 서버 테스트 실행
```bash
cd Room-Reservation
./gradlew test
```

### 2. Docker 환경에서 테스트 실행
```bash
docker exec -it room-reservation-server ./gradlew test
```

### 3. API 테스트 시나리오

#### 기본 예약 플로우
```bash
# 1. 회의실 조회
curl -X GET "http://localhost:8080/api/meeting-rooms"

# 2. 예약 생성
curl -X POST "http://localhost:8080/api/reservations" \
  -H "Content-Type: application/json" \
  -d '{
    "meetingRoomId": 1,
    "startTime": "2024-01-15T09:00:00",
    "endTime": "2024-01-15T10:00:00"
  }'

# 3. 결제 요청
curl -X POST "http://localhost:8080/api/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "reservationId": 1,
    "paymentMethod": "CARD"
  }'
```

---

## 🏗 클라우드 아키텍처 다이어그램


<img width="1732" height="818" alt="Image" src="https://github.com/user-attachments/assets/d0dc5324-678e-4de8-abb2-845d90a2e60e" />

---

## 🎯 설계 결정 사항

### 1. 전략 패턴 적용
- **결제 수단별 처리 로직 분리**: `PaymentStrategy` 인터페이스와 구현체들
- **확장성**: 새로운 결제 수단 추가 시 기존 코드 수정 없이 확장 가능
- **유지보수성**: 각 결제 수단별 로직이 독립적으로 관리됨

### 2. 팩토리 패턴 적용
- **결제 전략 생성**: `PaymentStrategyFactory`를 통한 결제 수단별 전략 객체 생성
- **의존성 분리**: 클라이언트 코드에서 구체적인 전략 클래스에 대한 의존성 제거
- **유연성**: 런타임에 결제 수단에 따라 적절한 전략 선택

### 3. 웹훅 처리 설계
- **비동기 처리**: `@Transactional(propagation = Propagation.REQUIRES_NEW)`로 메인 트랜잭션과 분리
- **멱등성 보장**: 동일한 웹훅 중복 처리 방지
- **예약 상태 동기화**: 결제 성공 시 자동으로 예약 상태 변경

### 4. 예약 중복 방지
- **시간대 검증**: 예약 시간대 중복 체크
- **비즈니스 로직**: 예약 생성 시점에 실시간 검증
- **데이터 일관성**: JPA 트랜잭션으로 데이터 무결성 보장

### 5. 예외 처리 전략
- **글로벌 예외 핸들러**: 일관된 에러 응답 형식
- **비즈니스 예외 분리**: 도메인별 예외 클래스 정의
- **로깅**: 상세한 에러 로그로 디버깅 지원

---

## 📁 프로젝트 구조

```
Room-Reservation/
├── Room-Reservation/          # 메인 예약 서버
│   ├── src/main/java/
│   │   └── com/room/reservation/system/
│   │       ├── api/           # API 레이어
│   │       │   ├── controller/    # REST API 컨트롤러
│   │       │   ├── service/       # 비즈니스 로직
│   │       │   │   └── payment/   # 결제 전략 패턴
│   │       │   ├── dto/           # 데이터 전송 객체
│   │       │   └── persistence/   # 데이터 접근 계층
│   │       │       ├── entity/    # JPA 엔티티
│   │       │       └── repository/ # Spring Data Repository
│   │       └── global/        # 글로벌 설정
│   │           └── error/     # 예외 처리
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-local.yml
│       └── data.sql          # 초기 데이터
├── Mock-Payment-Server/       # Mock 결제 서버
│   ├── src/main/java/
│   │   └── com/example/demo/
│   │       ├── controller/    # 결제 API 컨트롤러
│   │       ├── service/       # 결제 처리 서비스
│   │       ├── dto/           # 결제 DTO
│   │       └── config/        # 결제사 설정
│   └── src/main/resources/
│       ├── application.yml
│       └── application-dev.yml
├── docker-compose.yml        # 컨테이너 오케스트레이션
├── .gitignore
└── README.md
```

---

## 🔧 개발 환경 설정

### 필수 요구사항
- Docker & Docker Compose
- Java 17+
- Gradle 8.5+

### 권장 개발 도구
- IntelliJ IDEA / Eclipse
- Postman / Insomnia
- MySQL Workbench


