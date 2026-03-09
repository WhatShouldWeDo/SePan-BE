# 행정동 단위 통계 데이터 수집/저장 계획

## Context
현재 35개 collector가 다양한 지역 단위(시도/시군구/행정동/텍스트주소)로 데이터를 수집 중이나, regionCode가 정규화되지 않고 hjdongVersionId도 null인 상태. 선거 캠프 플랫폼의 핵심은 **행정동(선거구 원자 단위) 기준 데이터 분석**이므로:

1. 행정동 단위 데이터는 행정동코드로 정규화하여 `adminLevel=HJDONG`으로 저장
2. 시군구 단위 데이터는 시군구코드로 정규화하여 `adminLevel=SIGUNGU`로 저장 (억지로 분배하지 않음)
3. 모든 데이터에 `hjdongVersionId`를 주입하여 어느 시점의 행정구역 기준인지 명확히 함
4. 근 5년(2021~2026) 데이터를 축적, 각 시점의 행정동 버전 기준으로 관리

**핵심 원칙:** 시군구 데이터는 시군구임을, 행정동 데이터는 행정동임을 명확히 구분하여 저장. 프론트에서 조회 시 `adminLevel`로 구분 가능.

---

## Phase 1: 행정동 마스터 데이터 구축

### 1-1. 연도별 행정동 코드 확보
- **행정안전부 행정표준코드관리시스템(code.go.kr)** 에서 수동 다운로드
- `resources/data/hjdong/hjdong_2021.csv` ~ `hjdong_2026.csv` 배치
- CSV 형식: `code(10자리),name,sigunguCode(5자리)`
- 시도/시군구 마스터도 같이 확보 (기존 `RegionInitializer`에서 이미 적재 중인지 확인 필요)

### 1-2. HjdongVersion 6개 생성 + 행정동 데이터 로딩
- **신규:** `region/application/service/HjdongDataInitializer.java`
- v2021~v2026 버전 생성, v2026을 `isActive=true`
- 각 버전별 CSV에서 `Hjdong` 엔티티 적재
- 기존 `RegionInitializer` 패턴 참고, `@Order(2)` 실행

### 1-3. 법정동-행정동 매핑 파일
- 아파트 실거래가 등 법정동코드(LAWD_CD) 데이터 변환용
- `resources/data/bjdong_hjdong_mapping.csv` 배치
- 형식: `bjdongCode(10자리),hjdongCode(10자리)`

### 1-4. 연도간 행정동 변경 매핑
- `resources/data/hjdong/mapping_2021_2022.csv` ~ `mapping_2025_2026.csv`
- 형식: `sourceCode,targetCode,mappingType,ratio,description`
- 기존 `HjdongMapping` + `HjdongMappingRepository.saveAll()` 활용

---

## Phase 2: regionCode 정규화 서비스

### 2-1. `RegionCodeResolver` 신규 생성
**위치:** `statistics/application/service/RegionCodeResolver.java`

| 현재 형태 | 예시 | 변환 결과 | adminLevel |
|-----------|------|----------|------------|
| KOSIS C1 7자리 | "1101053" | "1101053000" (10자리) | HJDONG |
| KOSIS C1 5자리 | "11010" | "11010" (5자리) | SIGUNGU |
| KOSIS C1 2자리 | "11" | "11" | SIDO |
| "시도 시군구" 텍스트 | "서울특별시 종로구" | "11010" | SIGUNGU |
| "시도 시군구" 약칭 | "서울 광진구" | "11050" | SIGUNGU |
| V-World signgu_se | "48730" | "48730" | SIGUNGU |
| safemap sgg_cd | "44210" | "44210" | SIGUNGU |
| 법정동코드 10자리 | "1111010100" | 행정동코드 10자리 | HJDONG |
| 주소 텍스트 | "충청북도 청주시 상당구..." | "43110" | SIGUNGU |

**구현:**
- `@PostConstruct`로 Sido/Sigungu 테이블에서 `Map<String, String>` (이름→코드) 캐시 구축
- 약칭 맵: "서울"→"서울특별시", "경기"→"경기도", "강원"→"강원특별자치도" 등
- `normalize(String rawCode, EAdminLevel originalLevel)` → `NormalizedRegion(code, adminLevel)` 반환

---

## Phase 3: DataCollectionService 파이프라인 확장

### 3-1. `collectSingle()` 후처리 단계 추가

기존 흐름: `collect → save`
변경 흐름:
```
1. collect (기존)
2. hjdongVersionId 주입 (dataYear → 해당 연도의 HjdongVersion 매핑)
3. regionCode 정규화 (RegionCodeResolver)
   - 원본 regionCode → 정규화된 코드로 변환
   - adminLevel도 실제 데이터에 맞게 조정
4. save (기존)
```

**분배(distribute)는 하지 않음.** 시군구 데이터는 시군구로, 행정동 데이터는 행정동으로 그대로 저장.

### 3-2. 메타 정보 보존
`data` Map에 원본 정보 추가 (도메인 모델 변경 없음):
- `data.original_region_code`: 정규화 전 원본 코드 (예: "서울특별시 종로구")
- `data.original_admin_level`: 원본 adminLevel

### 3-3. 연도→버전 매핑 로직
```java
// dataYear "2023" → effectiveDate ≤ 2023-01-01인 최신 버전
HjdongVersion resolveVersionForYear(String dataYear)
```

### 3-4. 멀티연도 수집 API
```
POST /api/v1/data-collection/multi-year
{ "startYear": "2021", "endYear": "2026" }
```
- 연도별 순차 실행
- 각 연도에 맞는 HjdongVersion 자동 매핑

---

## Phase 4: Collector별 데이터 단위 분류

### 행정동(HJDONG) 단위 - regionCode가 행정동코드로 저장됨
| Collector | 소스 | regionCode 형태 |
|-----------|------|----------------|
| 읍면동별 총인구 | KOSIS DT_1B04005N | C1 7자리 → 10자리 정규화 |
| 가구형태별 가구 | KOSIS DT_1GA0501 | C1 7자리 → 10자리 정규화 |
| 아파트 매매 실거래가 | data.go.kr | LAWD_CD → 법정동→행정동 매핑 |
| 아파트 전월세 | data.go.kr | LAWD_CD → 법정동→행정동 매핑 |

### 시군구(SIGUNGU) 단위
| Collector | 소스 | regionCode 형태 |
|-----------|------|----------------|
| 연령별 인구 | KOSIS | C1 5자리 그대로 |
| 성별 인구 | KOSIS | C1 5자리 그대로 |
| V-World 전체 (10개) | V-World | 텍스트→시군구코드 변환 |
| safemap (2개) | safemap | sgg_cd→시군구코드 |
| 공동주택단지 | data.go.kr | sigunguCode 그대로 |
| 전통시장 | V-World | 주소→시군구코드 |
| 버스정류소 | data.go.kr | cityCode→시도, 개별 정류소는 시군구 |

### 시도(SIDO) 단위
| Collector | 소스 |
|-----------|------|
| 다문화가구 | KOSIS |
| 인구동태 | KOSIS |
| 기업재무정보 | data.go.kr |

### 특수 (지역 기반이 아닌 데이터)
| Collector | 비고 |
|-----------|------|
| 선거공약/정당정책/후보자정보 | 후보자/정당 단위 |
| 개표결과/선거인수/사전투표 | 선거구 단위 |
| 지방재정자립도 | 시군구 단위로 매핑 가능 |

---

## Phase 5: 과거 데이터 지원 현황

| Collector | 과거 5년 | 파라미터 |
|-----------|---------|---------|
| KOSIS 전체 | O | startPrdDe/endPrdDe |
| 아파트 매매/전월세 | O | DEAL_YMD |
| V-World 전체 | X | 현재 시점만 |
| safemap 전체 | X | 현재 시점만 |
| NEC 선관위 | O | sgId |
| NEIS/커리어넷 | X | 현재 시점만 |
| LOFIN | O | fis_yyyy |

→ **과거 데이터 미지원 소스는 최신 1회만 수집** (정확성 원칙)

---

## 수정 대상 파일

| 파일 | 작업 |
|------|------|
| `region/application/service/HjdongDataInitializer.java` | **신규** - 행정동 마스터 CSV 로딩 |
| `statistics/application/service/RegionCodeResolver.java` | **신규** - regionCode 정규화 |
| `statistics/application/service/DataCollectionService.java` | collectSingle() 파이프라인 확장 (hjdongVersionId 주입 + 정규화) |
| `statistics/adapter/in/web/command/DataCollectionController.java` | 멀티연도 수집 API 추가 |
| `resources/data/hjdong/*.csv` | **신규** - 행정동 마스터/매핑 CSV |
| `resources/data/bjdong_hjdong_mapping.csv` | **신규** - 법정동→행정동 매핑 |

---

## 구현 순서

1. **P0: 행정동 CSV 데이터 수집** (수동 - code.go.kr에서 2021~2026 다운로드)
2. **P1: HjdongDataInitializer** - CSV 로딩 + 6개 버전 생성
3. **P2: RegionCodeResolver** - regionCode 정규화 서비스
4. **P3: DataCollectionService 확장** - hjdongVersionId 주입 + 정규화 파이프라인
5. **P4: 법정동-행정동 매핑** - 부동산 데이터용
6. **P5: 행정동 변경 매핑 CSV** - 연도간 변경이력
7. **P6: 멀티연도 수집 API** - 5개년 일괄 수집

---

## 검증 방법

1. `./gradlew compileJava` - 빌드 확인
2. 앱 실행 후 행정동 마스터 적재 확인: `GET /api/v1/hjdong-versions`
3. 단일 수집 테스트: `POST /api/v1/data-collection/item?category=VOTER_INFO&itemName=읍면동별 총인구&year=2025`
4. MongoDB 확인: regionCode가 정규화된 코드로 저장 + hjdongVersionId가 채워져 있는지
5. adminLevel 확인: HJDONG/SIGUNGU/SIDO가 데이터 원본에 맞게 올바르게 설정되는지
