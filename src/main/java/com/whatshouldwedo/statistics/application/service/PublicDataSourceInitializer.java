package com.whatshouldwedo.statistics.application.service;

import com.whatshouldwedo.statistics.application.port.out.PublicDataSourceRepository;
import com.whatshouldwedo.statistics.domain.PublicDataSource;
import com.whatshouldwedo.statistics.domain.PublicDataSourceId;
import com.whatshouldwedo.statistics.domain.type.EDataFormat;
import com.whatshouldwedo.statistics.domain.type.EDataProvider;
import com.whatshouldwedo.statistics.domain.type.EDataSourcePriority;
import com.whatshouldwedo.statistics.domain.type.ERegionUnitType;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.whatshouldwedo.statistics.domain.type.EDataFormat.*;
import static com.whatshouldwedo.statistics.domain.type.EDataProvider.*;
import static com.whatshouldwedo.statistics.domain.type.EDataSourcePriority.*;
import static com.whatshouldwedo.statistics.domain.type.ERegionUnitType.*;
import static com.whatshouldwedo.statistics.domain.type.EStatisticsCategory.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataSourceInitializer implements ApplicationRunner {

    private final PublicDataSourceRepository repository;

    // ========== 00 선거분석 ==========
    @Value("${public-data.source.election-pledge.api-url}")
    private String electionPledgeApiUrl;
    @Value("${public-data.source.election-pledge.service-url}")
    private String electionPledgeServiceUrl;

    @Value("${public-data.source.party-policy.api-url}")
    private String partyPolicyApiUrl;
    @Value("${public-data.source.party-policy.service-url}")
    private String partyPolicyServiceUrl;

    @Value("${public-data.source.candidate.api-url}")
    private String candidateApiUrl;

    @Value("${public-data.source.elector-count.api-url}")
    private String electorCountApiUrl;
    @Value("${public-data.source.elector-count.service-url}")
    private String electorCountServiceUrl;

    @Value("${public-data.source.vote-count-result.api-url}")
    private String voteCountResultApiUrl;

    @Value("${public-data.source.early-voting.api-url}")
    private String earlyVotingApiUrl;

    // ========== 01 유권자정보 ==========
    @Value("${public-data.source.population-by-age-gender.api-url}")
    private String populationByAgeGenderApiUrl;

    @Value("${public-data.source.multicultural-household.api-url}")
    private String multiculturalHouseholdApiUrl;

    @Value("${public-data.source.population-dynamics.api-url}")
    private String populationDynamicsApiUrl;

    @Value("${public-data.source.household-type.api-url}")
    private String householdTypeApiUrl;

    @Value("${public-data.source.foreign-resident.api-url}")
    private String foreignResidentApiUrl;

    // ========== 02 재경 ==========
    @Value("${public-data.source.convenience-store.api-url}")
    private String convenienceStoreApiUrl;

    @Value("${public-data.source.corporate-financial.api-url}")
    private String corporateFinancialApiUrl;

    @Value("${public-data.source.major-commercial-district.api-url}")
    private String majorCommercialDistrictApiUrl;

    @Value("${public-data.source.major-commercial-district-polygon.api-url}")
    private String majorCommercialDistrictPolygonApiUrl;

    @Value("${public-data.source.traditional-market-polygon.api-url}")
    private String traditionalMarketPolygonApiUrl;

    @Value("${public-data.source.fiscal-independence.api-url}")
    private String fiscalIndependenceApiUrl;

    // ========== 03 주거부동산 ==========
    @Value("${public-data.source.apartment-sale-price.api-url}")
    private String apartmentSalePriceApiUrl;

    @Value("${public-data.source.apartment-complex.api-url}")
    private String apartmentComplexApiUrl;

    @Value("${public-data.source.apartment-rent-price.api-url}")
    private String apartmentRentPriceApiUrl;

    @Value("${public-data.source.urban-area-polygon.api-url}")
    private String urbanAreaPolygonApiUrl;

    @Value("${public-data.source.residential-improvement-district.api-url}")
    private String residentialImprovementDistrictApiUrl;

    @Value("${public-data.source.land-use-plan-polygon.api-url}")
    private String landUsePlanPolygonApiUrl;

    @Value("${public-data.source.national-land-planning-zone.api-url}")
    private String nationalLandPlanningZoneApiUrl;

    // ========== 04 교통 ==========
    @Value("${public-data.source.traffic-flow.api-url}")
    private String trafficFlowApiUrl;

    @Value("${public-data.source.bus-stop.api-url}")
    private String busStopApiUrl;

    @Value("${public-data.source.transport-facility-polygon.api-url}")
    private String transportFacilityPolygonApiUrl;

    // ========== 05 사회안전 ==========
    @Value("${public-data.source.crime-warning-zone.api-url}")
    private String crimeWarningZoneApiUrl;

    @Value("${public-data.source.fire-station-jurisdiction.api-url}")
    private String fireStationJurisdictionApiUrl;

    @Value("${public-data.source.drug-crime-statistics.api-url}")
    private String drugCrimeStatisticsApiUrl;

    @Value("${public-data.source.disaster-risk-zone-polygon.api-url}")
    private String disasterRiskZonePolygonApiUrl;

    @Value("${public-data.source.seoul-construction-project.api-url}")
    private String seoulConstructionProjectApiUrl;

    // ========== 06 복지분배 ==========
    @Value("${public-data.source.senior-welfare-facility.api-url}")
    private String seniorWelfareFacilityApiUrl;

    @Value("${public-data.source.child-welfare-facility-polygon.api-url}")
    private String childWelfareFacilityPolygonApiUrl;

    @Value("${public-data.source.senior-welfare-facility-polygon.api-url}")
    private String seniorWelfareFacilityPolygonApiUrl;

    @Value("${public-data.source.dog-park-polygon.api-url}")
    private String dogParkPolygonApiUrl;

    @Value("${public-data.source.group-cafeteria.api-url}")
    private String groupCafeteriaApiUrl;

    // ========== 09 교육 ==========
    @Value("${public-data.source.neis-school-basic-info.api-url}")
    private String neisSchoolBasicInfoApiUrl;

    @Value("${public-data.source.careernet-school-info.api-url}")
    private String careernetSchoolInfoApiUrl;

    // ========== 기타 ==========
    @Value("${public-data.source.tourist-attraction.api-url}")
    private String touristAttractionApiUrl;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("공공데이터 소스 초기화 시작...");

        // ========== 00 선거분석 ==========
        seed(1, "선거공약 정보", ELECTION_ANALYSIS, NEC,
                electionPledgeApiUrl,
                electionPledgeServiceUrl,
                Set.of(JSON), Set.of(ELECTORAL_DISTRICT, SIGUNGU, HJDONG), PRIORITY,
                "시도,시군구,선거구별 정당 및 공약. 선거별 각 정당 출마자의 지역 정책 공약 분석 기초");

        seed(5, "정당정책 정보", ELECTION_ANALYSIS, NEC,
                partyPolicyApiUrl,
                partyPolicyServiceUrl,
                Set.of(JSON), Set.of(ELECTORAL_DISTRICT), PRIORITY,
                "각 선거별 각 정당의 공약순번, 공약분야명, 공약내용. 선거별 각 중앙당의 주요 정강정책 분석 기초");

        seed(6, "후보자 정보", ELECTION_ANALYSIS, NEC,
                candidateApiUrl, null,
                Set.of(JSON), Set.of(ELECTORAL_DISTRICT), WAITING,
                "선거별 후보자의 성명, 생년월일, 성별, 선거구, 학력, 경력, 등록상태 등 상세 정보. 역대 출마자 현황 조회");

        seed(7, "선거인수 정보", ELECTION_ANALYSIS, NEC,
                electorCountApiUrl,
                electorCountServiceUrl,
                Set.of(JSON), Set.of(ELECTORAL_DISTRICT, VOTING_DISTRICT, SIGUNGU, HJDONG), CONSIDERING,
                "선거구 내 투표구별 인구수, 확정선거인수(남/여/재외국민/외국인). 행정동 단위까지 분석결과 도출");

        seed(8, "개표결과", ELECTION_ANALYSIS, NEC,
                voteCountResultApiUrl, null,
                Set.of(CSV, JSON), Set.of(ELECTORAL_DISTRICT, VOTING_DISTRICT, SIGUNGU, HJDONG), PRIORITY,
                "선거구,행정동,투표구의 후보자별 관내사전투표/관외사전투표/본투표 득표수. 투표구별 득표수 계산 및 정당별 유불리 표현");

        seed(9, "사전투표 정보", ELECTION_ANALYSIS, NEC,
                earlyVotingApiUrl, null,
                Set.of(JSON), Set.of(ELECTORAL_DISTRICT, VOTING_DISTRICT, SIGUNGU, HJDONG), WAITING,
                "사전투표 구분 일차별 사전투표율");

        // ========== 01 유권자정보 ==========
        seed(38, "인구총조사 연령 및 성별 인구(읍면동)", VOTER_INFO, KOSIS,
                populationByAgeGenderApiUrl, null,
                Set.of(JSON), Set.of(HJDONG), WAITING,
                "행정동별 성별,연령대별 인구. 전수조사자료");

        seed(39, "읍면동별 다문화가구 현황", VOTER_INFO, KOSIS,
                multiculturalHouseholdApiUrl, null,
                Set.of(JSON), Set.of(HJDONG), WAITING,
                "지방자치단체 외국인주민현황: 읍면동별 다문화가구 현황");

        seed(41, "읍면동/성별 인구동태건수(출생,사망,혼인,이혼)", VOTER_INFO, KOSIS,
                populationDynamicsApiUrl, null,
                Set.of(JSON), Set.of(HJDONG), WAITING,
                "읍면동별 출생, 사망, 혼인, 이혼 건수");

        seed(42, "가구형태별 가구 및 가구원(읍면동)", VOTER_INFO, KOSIS,
                householdTypeApiUrl, null,
                Set.of(CSV), Set.of(HJDONG), WAITING,
                "2005년 전수자료 확인 가능. 최근 전수조사 자료 통계청 확인 예정");

        seed(43, "읍면동별 유형 및 지역별 외국인 주민현황", VOTER_INFO, KOSIS,
                foreignResidentApiUrl, null,
                Set.of(JSON), Set.of(HJDONG), WAITING,
                "읍면동별 외국인 주민 유형 및 지역별 현황");

        // ========== 02 재경 ==========
        seed(15, "편의점", ECONOMY, GOV_MINISTRY,
                convenienceStoreApiUrl, null,
                Set.of(), Set.of(COORDINATES, ADDRESS), CONSIDERING,
                "행정안전부 편의점 위치 정보");

        seed(16, "기업 재무정보", ECONOMY, GOV_MINISTRY,
                corporateFinancialApiUrl, null,
                Set.of(), Set.of(), CONSIDERING,
                "기업 매출액, 영업이익, 총자산, 총부채, 자본금. 기업 규모별 기업수");

        seed(18, "주요상권", ECONOMY, GOV_MINISTRY,
                majorCommercialDistrictApiUrl, null,
                Set.of(), Set.of(ADDRESS), PRIORITY,
                "주소기반 행정동 변경. 지역 내 주요상권 리스트업");

        seed(32, "주요상권(폴리곤)", ECONOMY, GOV_MINISTRY,
                majorCommercialDistrictPolygonApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "주요상권 공간정보(폴리곤)");

        seed(33, "전통시장현황(폴리곤)", ECONOMY, GOV_MINISTRY,
                traditionalMarketPolygonApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "전통시장 공간정보(폴리곤)");

        seed(37, "지방재정365 재정자립도(결산)", ECONOMY, LOCAL_GOV,
                fiscalIndependenceApiUrl, null,
                Set.of(JSON), Set.of(SIGUNGU), WAITING,
                "기초자치단체별 재정자립도(개편후) 현황, 시계열");

        // ========== 03 주거부동산 ==========
        seed(10, "아파트 매매 실거래가 자료", HOUSING, GOV_MINISTRY,
                apartmentSalePriceApiUrl, null,
                Set.of(JSON), Set.of(ADDRESS), WAITING,
                "법정동+지번 → 행정동 변경 필요. 건축년도별 노후년수대별 거래현황, 면적당 시세, 토지임대부 아파트 리스트");

        seed(11, "공동주택 단지 목록", HOUSING, GOV_MINISTRY,
                apartmentComplexApiUrl, null,
                Set.of(JSON), Set.of(ADDRESS), WAITING,
                "국토교통부 공동주택 단지 목록제공 서비스");

        seed(20, "아파트 전월세 실거래가 자료", HOUSING, GOV_MINISTRY,
                apartmentRentPriceApiUrl, null,
                Set.of(), Set.of(ADDRESS), WAITING,
                "법정동+지번 → 행정동 변경. 계약연도별 건축년도, 계약기간, 계약구분, 전용면적. 전월세 비율 파악");

        seed(25, "도시지역(폴리곤)", HOUSING, GOV_MINISTRY,
                urbanAreaPolygonApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "선거구(행정동)내 도시용도지역별 비율, 용도지구 지정현황. 재개발재건축 잠재 지역 도출");

        seed(26, "주거환경개선지구도(폴리곤)", HOUSING, GOV_MINISTRY,
                residentialImprovementDistrictApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "주거환경개선지구 공간정보");

        seed(27, "토지이용계획도(폴리곤)", HOUSING, GOV_MINISTRY,
                landUsePlanPolygonApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "주택건설용지(공동주택,단독주택,근린생활시설)와 공공시설용지의 토지이용상황");

        seed(28, "국토계획구역(폴리곤)", HOUSING, GOV_MINISTRY,
                nationalLandPlanningZoneApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "용도지역명 공간정보");

        // ========== 04 교통 ==========
        seed(14, "교통소통정보", TRANSPORT, GOV_MINISTRY,
                trafficFlowApiUrl, null,
                Set.of(), Set.of(ADDRESS), CONSIDERING,
                "통행속도, 통행시간 바탕 혼잡도 정보. 혼잡도로별 도로관리기관 → 정책개발 시 협의 추진 활용");

        seed(19, "TAGO 버스정류소정보", TRANSPORT, GOV_MINISTRY,
                busStopApiUrl, null,
                Set.of(), Set.of(COORDINATES), CONSIDERING,
                "정류소 좌표 → 행정동 변경. 경유 노선 수");

        seed(35, "도시계획 교통시설(폴리곤)", TRANSPORT, GOV_MINISTRY,
                transportFacilityPolygonApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "도시계획시설(도로,유통공급시설,공공문화체육시설,방재시설 등) 공간정보");

        // ========== 05 사회안전 ==========
        seed(12, "생활안전지도 범죄주의구간(WMS)", SAFETY, GOV_MINISTRY,
                crimeWarningZoneApiUrl, null,
                Set.of(JSON), Set.of(ADDRESS), CONSIDERING,
                "범죄별 밀도분석 공간정보");

        seed(21, "소방서관할구역", SAFETY, GOV_MINISTRY,
                fireStationJurisdictionApiUrl, null,
                Set.of(), Set.of(ADDRESS), WAITING,
                "소방서 위치 파악");

        seed(24, "생활안전지도 치안사고통계(마약, WMS)", SAFETY, GOV_MINISTRY,
                drugCrimeStatisticsApiUrl, null,
                Set.of(), Set.of(ADDRESS), WAITING,
                "경찰관서별 발생 건수를 5개 등급으로 구분. 관서 관할별 9대발생통계 강도 활용");

        seed(31, "재해위험지구(폴리곤)", SAFETY, GOV_MINISTRY,
                disasterRiskZonePolygonApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "재해위험지구 지정개소 현황 및 비율");

        seed(44, "서울시 건설알림이 사업개요", SAFETY, LOCAL_GOV,
                seoulConstructionProjectApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "서울시 기반시설사업 공사개요. 지역구 기반시설 건설사업 현황 파악");

        // ========== 06 복지분배 ==========
        seed(13, "노인복지시설", WELFARE, GOV_MINISTRY,
                seniorWelfareFacilityApiUrl, null,
                Set.of(), Set.of(ADDRESS), PRIORITY,
                "시설새주소 → 행정동 변경. 노인복지시설 리스트업 및 count");

        seed(29, "아동복지시설(폴리곤)", WELFARE, GOV_MINISTRY,
                childWelfareFacilityPolygonApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "지역 내 아동복지시설 현황 및 아동인구대비 시설보유율(아동양육,보호치료,직업훈련,자립지원,단기보호,상담소,전용시설,복지관,공동생활가정,지역아동센터)");

        seed(30, "노인복지시설(폴리곤)", WELFARE, GOV_MINISTRY,
                seniorWelfareFacilityPolygonApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "지역내 노인복지시설 현황 및 시설분류별, 민관별 보유율");

        seed(34, "반려견놀이터(폴리곤)", WELFARE, GOV_MINISTRY,
                dogParkPolygonApiUrl, null,
                Set.of(JSON), Set.of(POLYGON), WAITING,
                "반려견놀이터 개소 현황 및 면적 비율(시도명,시군구명,놀이터명,운영시간,휴무일,시설현황,면적)");

        seed(36, "집단급식소 설치 현황", WELFARE, GOV_MINISTRY,
                groupCafeteriaApiUrl, null,
                Set.of(JSON), Set.of(ADDRESS), WAITING,
                "급식소주소(지번주소) 활용. 집단구분: 학교, 기업체 현황. 무상급식, 천원의아침밥 공급 검토");

        // ========== 09 교육 ==========
        seed(17, "나이스(NEIS) 초중등 학교기본정보", EDUCATION, GOV_MINISTRY,
                neisSchoolBasicInfoApiUrl, null,
                Set.of(), Set.of(ADDRESS), PRIORITY,
                "주소기반 행정동 변경. 선거구별 각급학교 개수 및 위치");

        seed(23, "커리어넷 학교정보", EDUCATION, GOV_MINISTRY,
                careernetSchoolInfoApiUrl, null,
                Set.of(), Set.of(ADDRESS), WAITING,
                "지역구내 국립, 사립, 공립 각급 학교 현황, 대안학교 현황");

        // ========== 기타 (구분 없음) ==========
        seed(22, "관광지", CULTURE, GOV_MINISTRY,
                touristAttractionApiUrl, null,
                Set.of(), Set.of(SIGUNGU), WAITING,
                "시군구별 주요 관광지");

        log.info("공공데이터 소스 초기화 완료");
    }

    private void seed(int sourceId, String name, EStatisticsCategory category,
                      EDataProvider provider, String apiUrl, String serviceUrl,
                      Set<EDataFormat> formats, Set<ERegionUnitType> regionUnits,
                      EDataSourcePriority priority, String notes) {
        if (repository.existsBySourceId(sourceId)) {
            return;
        }
        PublicDataSource ds = PublicDataSource.create(
                PublicDataSourceId.generate(), sourceId, name, category, provider,
                apiUrl, serviceUrl, formats, regionUnits, priority, notes
        );
        repository.save(ds);
        log.debug("  [{}] {} 등록 완료", sourceId, name);
    }
}
