# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./gradlew build                    # 빌드
./gradlew bootRun                  # 로컬 실행 (기본 profile: local)
./gradlew test                     # 전체 테스트
./gradlew test --tests "클래스명"   # 단일 테스트 실행
./gradlew clean build              # 클린 빌드
```

- Java 21, Spring Boot 3.2.2, Gradle
- Profiles: `local`, `dev`, `prod` (application-{profile}.yml)
- Local 환경: MySQL (localhost:3306/sepan), MongoDB (localhost:27017/sepan), Redis

## Architecture

**Hexagonal Architecture (Ports & Adapters)** 패턴을 따르는 프로젝트. 각 도메인 모듈은 다음 구조를 가짐:

```
{domain}/
├── domain/           # 도메인 모델, ID 값 객체(BaseId<T> 상속), enum types
├── application/
│   ├── port/
│   │   ├── in/       # UseCase 인터페이스, Input(command/query), Output(result)
│   │   └── out/      # Repository 포트 인터페이스
│   └── service/      # UseCase 구현체 (@UseCase 어노테이션)
└── adapter/
    ├── in/web/       # Controller (command/query 분리), RequestDto
    └── out/
        ├── persistence/  # Repository 구현 (JPA Entity, JPA Repository, PersistenceAdapter)
        ├── mongodb/      # MongoDB 저장소 (statistics 모듈)
        └── external/     # 외부 API 클라이언트
```

## Domain Modules

- **user**: 사용자 관리
- **security**: 인증/인가 (JWT, Spring Security, stateless session)
- **region**: 행정구역 (시도/시군구/행정동, 버전 관리 및 매핑)
- **election**: 선거 및 선거구 관리
- **statistics**: 통계 데이터 (JPA + MongoDB 이중 저장소, 공공데이터 API 연동)
- **policy**: 공약 및 AI 추천
- **core**: 공통 인프라 (ResponseDto, ErrorCode, BaseId, 유틸리티, 예외 처리)

## Key Conventions

- **Controller**: Command/Query 분리 (`{Domain}CommandController`, `{Domain}QueryController`)
- **응답**: 모든 API 응답은 `ResponseDto<T>`로 래핑 (`ResponseDto.ok()`, `.created()`, `.fail()`)
- **서비스**: `@UseCase` 커스텀 어노테이션 사용 (Spring `@Component` 기반)
- **도메인 ID**: `BaseId<T>` 상속하는 값 객체 (예: `UserId`, `ElectionId`), UUID 기반
- **도메인 모델**: `create()` 정적 팩토리 메서드로 생성, `reconstitute()`로 DB에서 복원
- **JPA Entity ↔ Domain**: `fromDomain()` / `toDomain()` 메서드로 변환
- **에러**: `ErrorCode` enum에 도메인별 섹션으로 정의, `CommonException` 사용
- **인증 불필요 URL**: `Constants.NO_NEED_AUTH_URLS` / `NO_NEED_AUTH_GET_URLS`에 등록
- **API prefix**: `/api/v1/`
