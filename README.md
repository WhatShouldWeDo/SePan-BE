# SePan-BE

> 대한민국 선거 캠프를 위한 통합 데이터 분석 플랫폼 — Backend API Server

## Tech Stack

| 분류 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.2.2 |
| Build | Gradle 8.x |
| Database | MySQL 8.0, MongoDB 7.0, Redis |
| Auth | Spring Security, JWT (jjwt 0.12.3) |
| External API | Spring WebFlux (WebClient) |
| Batch | Spring Batch |
| Infra | Docker |

## Architecture

**Hexagonal Architecture (Ports & Adapters)** 패턴을 기반으로 설계되었습니다.

```
{domain}/
├── domain/              # 도메인 모델, ID 값 객체, Enum
├── application/
│   ├── port/
│   │   ├── in/          # UseCase 인터페이스, Input/Output DTO
│   │   └── out/         # Repository 포트 인터페이스
│   └── service/         # UseCase 구현체
└── adapter/
    ├── in/web/          # Controller (Command/Query 분리)
    └── out/
        ├── persistence/ # JPA Entity, Repository 구현
        ├── mongodb/     # MongoDB 저장소
        └── external/    # 외부 API 클라이언트
```

## Modules

| 모듈 | 설명 |
|------|------|
| **core** | 공통 인프라 — ResponseDto, ErrorCode, BaseId, 예외 처리, 유틸리티 |
| **user** | 사용자 관리 |
| **security** | 인증/인가 — JWT, Spring Security, Stateless Session |
| **region** | 행정구역 — 시도/시군구/행정동, 버전 관리 및 연도별 매핑 |
| **election** | 선거 및 선거구 관리 — 선거구-행정동 매핑 |
| **statistics** | 통계 데이터 — 공공데이터 API 연동, JPA + MongoDB 이중 저장소 |
| **policy** | 공약 관리 및 AI 추천 |

## Public Data Integration

35개 수집기를 통해 7개 공공데이터 API를 연동합니다.

| API | 수집 데이터 |
|-----|-----------|
| data.go.kr | 선관위(선거공약/후보자/정당정책/개표결과), 아파트 실거래, 버스정류소, 교통, 기업재무, 노인복지시설 |
| KOSIS (통계청) | 인구(연령별/성별), 가구형태, 다문화가구, 인구동태 |
| V-World (국토정보) | 상권, 전통시장, 복지시설, 도시계획, 도시재생 등 폴리곤/포인트 데이터 |
| safemap (생활안전지도) | 범죄주의구간, 치안사고통계 |
| 서울 열린데이터 | 건설알림이 사업개요 |
| 커리어넷 | 학교정보 |
| LOFIN (지방재정) | 재정자립도 |

## Getting Started

### Prerequisites

- Java 21
- MySQL 8.0
- MongoDB 7.0
- Redis

### Database Setup

```sql
CREATE DATABASE sepan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Configuration

`src/main/resources/application-local.yml`을 생성하고 아래 항목을 설정합니다:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sepan
    username: root
    password: <your-password>
  data:
    mongodb:
      uri: mongodb://localhost:27017/sepan

jwt:
  secret: <base64-encoded-secret>
  access-token-validity-ms: 3600000
  refresh-token-validity-ms: 604800000

public-data:
  data-go-kr:
    service-key: <your-key>
  vworld:
    api-key: <your-key>
  # ... 기타 API 키
```

### Build & Run

```bash
# 빌드
./gradlew build

# 로컬 실행
./gradlew bootRun

# 클린 빌드
./gradlew clean build
```

### Docker

```bash
./gradlew clean build
docker build -t sepan-be .
docker run -p 8080:8080 sepan-be
```

## API Endpoints

모든 API는 `/api/v1/` prefix를 사용합니다.

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/auth/signup` | 회원가입 |
| POST | `/api/v1/auth/login` | 로그인 |
| GET | `/api/v1/regions/sido` | 시도 목록 조회 |
| GET | `/api/v1/regions/sigungu` | 시군구 목록 조회 |
| GET | `/api/v1/hjdong-versions` | 행정동 버전 목록 |
| POST | `/api/v1/elections` | 선거 생성 |
| GET | `/api/v1/elections` | 선거 목록 조회 |
| POST | `/api/v1/data-collection/all` | 전체 데이터 수집 |
| POST | `/api/v1/data-collection/category` | 카테고리별 수집 |
| POST | `/api/v1/data-collection/item` | 개별 항목 수집 |
| GET | `/api/v1/statistics/records` | 통계 레코드 조회 |
| POST | `/api/v1/pledges` | 공약 생성 |
| GET | `/api/v1/pledges` | 공약 목록 조회 |

## Project Structure

```
src/main/
├── java/com/whatshouldwedo/
│   ├── core/               # 공통 인프라
│   ├── user/               # 사용자
│   ├── security/           # 인증/인가
│   ├── region/             # 행정구역
│   ├── election/           # 선거/선거구
│   ├── statistics/         # 통계 데이터 수집
│   └── policy/             # 공약/AI 추천
└── resources/
    ├── application.yml     # 프로필 설정
    └── data/               # 마스터 데이터 CSV
        ├── hjdong/         # 행정동 마스터 (2022~2026)
        ├── bjdong_hjdong_mapping.csv
        └── tago_city_codes.csv
```
