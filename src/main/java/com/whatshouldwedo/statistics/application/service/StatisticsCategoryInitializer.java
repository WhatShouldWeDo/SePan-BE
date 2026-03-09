package com.whatshouldwedo.statistics.application.service;

import com.whatshouldwedo.statistics.application.port.out.StatisticsCategoryRepository;
import com.whatshouldwedo.statistics.domain.StatisticsCategory;
import com.whatshouldwedo.statistics.domain.StatisticsCategoryId;
import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import com.whatshouldwedo.statistics.domain.type.ESurveyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.whatshouldwedo.region.domain.type.EAdminLevel.*;
import static com.whatshouldwedo.statistics.domain.type.EStatisticsCategory.*;
import static com.whatshouldwedo.statistics.domain.type.ESurveyType.*;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class StatisticsCategoryInitializer implements ApplicationRunner {

    private final StatisticsCategoryRepository repository;

    // NEC 선관위
    @Value("${public-data.api.nec.election-pledge}")
    private String necElectionPledgeApi;
    @Value("${public-data.api.nec.party-policy}")
    private String necPartyPolicyApi;
    @Value("${public-data.api.nec.candidate}")
    private String necCandidateApi;
    @Value("${public-data.api.nec.elector-count}")
    private String necElectorCountApi;
    @Value("${public-data.api.nec.vote-result}")
    private String necVoteResultApi;
    @Value("${public-data.api.nec.early-voting}")
    private String necEarlyVotingApi;

    // KOSIS
    @Value("${public-data.api.kosis.base-url}")
    private String kosisBaseUrl;

    // 재경
    @Value("${public-data.source.convenience-store.api-url}")
    private String convenienceStoreApiUrl;
    @Value("${public-data.source.corporate-financial.api-url}")
    private String corporateFinancialApiUrl;
    @Value("${public-data.source.major-commercial-district.api-url}")
    private String majorCommercialDistrictApiUrl;
    @Value("${public-data.source.traditional-market-polygon.api-url}")
    private String traditionalMarketPolygonApiUrl;
    @Value("${public-data.source.fiscal-independence.api-url}")
    private String fiscalIndependenceApiUrl;

    // 주거부동산
    @Value("${public-data.api.apt.trade}")
    private String aptTradeApi;
    @Value("${public-data.api.apt.complex}")
    private String aptComplexApi;
    @Value("${public-data.api.apt.rent}")
    private String aptRentApi;
    @Value("${public-data.source.urban-area-polygon.api-url}")
    private String urbanAreaPolygonApiUrl;
    @Value("${public-data.source.residential-improvement-district.api-url}")
    private String residentialImprovementDistrictApiUrl;
    @Value("${public-data.source.land-use-plan-polygon.api-url}")
    private String landUsePlanPolygonApiUrl;
    @Value("${public-data.source.national-land-planning-zone.api-url}")
    private String nationalLandPlanningZoneApiUrl;

    // 교통
    @Value("${public-data.api.traffic.info}")
    private String trafficInfoApi;
    @Value("${public-data.api.bus.stop}")
    private String busStopApi;
    @Value("${public-data.source.transport-facility-polygon.api-url}")
    private String transportFacilityPolygonApiUrl;

    // 사회안전
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

    // 복지
    @Value("${public-data.api.senior-welfare.facility}")
    private String seniorWelfareFacilityApi;
    @Value("${public-data.source.child-welfare-facility-polygon.api-url}")
    private String childWelfareFacilityPolygonApiUrl;
    @Value("${public-data.source.senior-welfare-facility-polygon.api-url}")
    private String seniorWelfareFacilityPolygonApiUrl;
    @Value("${public-data.source.dog-park-polygon.api-url}")
    private String dogParkPolygonApiUrl;
    @Value("${public-data.source.group-cafeteria.api-url}")
    private String groupCafeteriaApiUrl;

    // 문화
    @Value("${public-data.source.tourist-attraction.api-url}")
    private String touristAttractionApiUrl;

    // 교육
    @Value("${public-data.api.neis.school-info}")
    private String neisSchoolInfoApi;
    @Value("${public-data.api.careernet.school-info}")
    private String careernetSchoolInfoApi;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("통계 카테고리 초기화 시작...");

        // ========== 00 선거분석 (ELECTION_ANALYSIS) ==========
        seed(ELECTION_ANALYSIS, "선거공약", SIGUNGU, CENSUS, "건",
                necElectionPledgeApi,
                "선거별 후보자 공약 정보. 시도/시군구/선거구별 정당 및 공약 분석 기초 데이터");

        seed(ELECTION_ANALYSIS, "정당정책", SIDO, CENSUS, "건",
                necPartyPolicyApi,
                "선거별 각 정당의 주요 정강정책. 공약순번, 공약분야명, 공약내용");

        seed(ELECTION_ANALYSIS, "후보자정보", SIGUNGU, CENSUS, "명",
                necCandidateApi,
                "선거별 후보자 성명, 생년월일, 성별, 선거구, 학력, 경력, 등록상태 상세 정보");

        seed(ELECTION_ANALYSIS, "선거인수", HJDONG, CENSUS, "명",
                necElectorCountApi,
                "선거구 내 투표구별 인구수, 확정선거인수(남/여/재외국민). 행정동 단위 분석");

        seed(ELECTION_ANALYSIS, "개표결과", HJDONG, CENSUS, "표",
                necVoteResultApi,
                "선거구/행정동/투표구별 후보자 득표수. 관내사전투표/관외사전투표/본투표 득표 분석");

        seed(ELECTION_ANALYSIS, "사전투표율", HJDONG, CENSUS, "%",
                necEarlyVotingApi,
                "선거구별 사전투표 구분 일차별 사전투표율");

        // ========== 01 유권자정보 (VOTER_INFO) ==========
        seed(VOTER_INFO, "연령·성별 인구", HJDONG, CENSUS, "명",
                kosisBaseUrl,
                "행정동별 성별/연령대별 인구. 인구총조사 전수조사자료 (orgId=101, tblId=DT_1IN1503)");

        seed(VOTER_INFO, "다문화가구 현황", HJDONG, CENSUS, "가구",
                kosisBaseUrl,
                "읍면동별 다문화가구 현황. 지자체 외국인주민현황 (orgId=110, tblId=DT_110025_A045_A)");

        seed(VOTER_INFO, "인구동태(출생·사망·혼인·이혼)", HJDONG, CENSUS, "건",
                kosisBaseUrl,
                "읍면동별/성별 출생, 사망, 혼인, 이혼 건수 (orgId=101, tblId=DT_1B8000K)");

        seed(VOTER_INFO, "가구형태별 가구", HJDONG, CENSUS, "가구",
                kosisBaseUrl,
                "읍면동별 가구형태별 가구 및 가구원 수. 인구총조사 전수자료 (orgId=101, tblId=DT_1GA0501)");

        seed(VOTER_INFO, "외국인 주민현황", HJDONG, CENSUS, "명",
                kosisBaseUrl,
                "읍면동별 유형 및 지역별 외국인 주민 현황 (orgId=110, tblId=DT_110025_A033_A)");

        // ========== 02 재경 (ECONOMY) ==========
        seed(ECONOMY, "편의점 현황", SIGUNGU, CENSUS, "개소",
                convenienceStoreApiUrl,
                "행정안전부 편의점 위치 정보 (safemap LINK형, 현재 서비스 종료 상태)");

        seed(ECONOMY, "기업 재무정보", SIGUNGU, SAMPLE, "백만원",
                corporateFinancialApiUrl,
                "기업 매출액, 영업이익, 총자산, 총부채, 자본금. 기업 규모별 기업수 통계");

        seed(ECONOMY, "주요상권", SIGUNGU, CENSUS, "개소",
                majorCommercialDistrictApiUrl,
                "주소기반 행정동 변환 후 지역 내 주요상권 리스트업");

        seed(ECONOMY, "전통시장 현황", SIGUNGU, CENSUS, "개소",
                traditionalMarketPolygonApiUrl,
                "전통시장 공간정보 기반 지역별 전통시장 개소 현황");

        seed(ECONOMY, "지방재정자립도", SIGUNGU, CENSUS, "%",
                fiscalIndependenceApiUrl,
                "기초자치단체별 재정자립도(개편후) 현황, 시계열 데이터");

        // ========== 03 주거·부동산 (HOUSING) ==========
        seed(HOUSING, "아파트 매매 실거래가", SIGUNGU, CENSUS, "만원",
                aptTradeApi,
                "법정동+지번 기반 아파트 매매 실거래. 건축년도별/면적별 거래현황 및 시세 분석");

        seed(HOUSING, "공동주택 단지 목록", SIGUNGU, CENSUS, "개소",
                aptComplexApi,
                "국토교통부 공동주택 단지 목록. 지역별 아파트 단지 현황");

        seed(HOUSING, "아파트 전월세 실거래가", SIGUNGU, CENSUS, "만원",
                aptRentApi,
                "법정동+지번 기반 전월세 실거래. 전월세 비율 및 임대차 동향 분석");

        seed(HOUSING, "도시지역 용도", SIGUNGU, CENSUS, "㎡",
                urbanAreaPolygonApiUrl,
                "선거구(행정동)내 도시용도지역별 비율, 용도지구 지정현황. 재개발재건축 잠재 지역 도출");

        seed(HOUSING, "주거환경개선지구", SIGUNGU, CENSUS, "개소",
                residentialImprovementDistrictApiUrl,
                "주거환경개선지구 공간정보 기반 현황");

        seed(HOUSING, "토지이용계획", SIGUNGU, CENSUS, "㎡",
                landUsePlanPolygonApiUrl,
                "주택건설용지/공공시설용지 토지이용상황");

        seed(HOUSING, "국토계획구역", SIGUNGU, CENSUS, "㎡",
                nationalLandPlanningZoneApiUrl,
                "용도지역명 기반 국토계획구역 공간정보");

        // ========== 04 교통 (TRANSPORT) ==========
        seed(TRANSPORT, "교통소통정보", SIGUNGU, SAMPLE, "km/h",
                trafficInfoApi,
                "통행속도/통행시간 기반 혼잡도 정보. 혼잡도로별 도로관리기관 연계 정책개발 활용");

        seed(TRANSPORT, "버스정류소 현황", SIGUNGU, CENSUS, "개소",
                busStopApi,
                "정류소 좌표 기반 행정동 변환. 경유 노선 수 분석");

        seed(TRANSPORT, "도시계획 교통시설", SIGUNGU, CENSUS, "개소",
                transportFacilityPolygonApiUrl,
                "도시계획시설(도로,유통공급시설,공공문화체육시설,방재시설 등) 공간정보");

        // ========== 05 사회안전 (SAFETY) ==========
        seed(SAFETY, "범죄주의구간", SIGUNGU, CENSUS, "건",
                crimeWarningZoneApiUrl,
                "범죄별 밀도분석 공간정보. 지역별 범죄 발생 밀도 분석");

        seed(SAFETY, "소방서 관할구역", SIGUNGU, CENSUS, "개소",
                fireStationJurisdictionApiUrl,
                "소방서 위치 및 관할구역 정보 (V-World)");

        seed(SAFETY, "치안사고통계", SIGUNGU, CENSUS, "건",
                drugCrimeStatisticsApiUrl,
                "경찰관서별 9대 범죄 발생 건수. 5개 등급 구분 관서 관할별 발생통계");

        seed(SAFETY, "재해위험지구", SIGUNGU, CENSUS, "개소",
                disasterRiskZonePolygonApiUrl,
                "재해위험지구 지정개소 현황 및 비율");

        seed(SAFETY, "건설사업 현황", SIGUNGU, CENSUS, "건",
                seoulConstructionProjectApiUrl,
                "서울시 기반시설사업 공사개요. 지역구 기반시설 건설사업 현황 파악");

        // ========== 06 복지·분배 (WELFARE) ==========
        seed(WELFARE, "노인복지시설", SIGUNGU, CENSUS, "개소",
                seniorWelfareFacilityApi,
                "시설주소 기반 행정동 변환. 지역별 노인복지시설 리스트업 및 count");

        seed(WELFARE, "아동복지시설", SIGUNGU, CENSUS, "개소",
                childWelfareFacilityPolygonApiUrl,
                "지역내 아동복지시설 현황 및 아동인구대비 시설보유율");

        seed(WELFARE, "노인복지시설(폴리곤)", SIGUNGU, CENSUS, "개소",
                seniorWelfareFacilityPolygonApiUrl,
                "지역내 노인복지시설 현황 및 시설분류별/민관별 보유율");

        seed(WELFARE, "반려견놀이터", SIGUNGU, CENSUS, "개소",
                dogParkPolygonApiUrl,
                "반려견놀이터 개소 현황 및 면적 비율");

        seed(WELFARE, "집단급식소", SIGUNGU, CENSUS, "개소",
                groupCafeteriaApiUrl,
                "급식소주소(지번주소) 활용. 학교/기업체 집단급식소 현황");

        // ========== 07 문화·여가 (CULTURE) ==========
        seed(CULTURE, "관광지", SIGUNGU, CENSUS, "개소",
                touristAttractionApiUrl,
                "시군구별 주요 관광지 현황 (V-World 용도지역지구)");

        // ========== 09 교육·훈련 (EDUCATION) ==========
        seed(EDUCATION, "초중등학교 기본정보", SIGUNGU, CENSUS, "개교",
                neisSchoolInfoApi,
                "주소기반 행정동 변환. 선거구별 각급학교 개수 및 위치");

        seed(EDUCATION, "커리어넷 학교정보", SIGUNGU, CENSUS, "개교",
                careernetSchoolInfoApi,
                "지역구내 국립/사립/공립 각급 학교 현황, 대안학교 현황");

        log.info("통계 카테고리 초기화 완료 - 총 {}개 카테고리", repository.findAll().size());
    }

    private void seed(EStatisticsCategory category, String name, EAdminLevel dataLevel,
                      ESurveyType surveyType, String unit, String sourceApiUrl, String description) {
        if (repository.existsByCategoryAndName(category, name)) {
            return;
        }
        StatisticsCategory cat = StatisticsCategory.create(
                StatisticsCategoryId.generate(), category, name, dataLevel, surveyType,
                unit, sourceApiUrl, description
        );
        repository.save(cat);
        log.debug("  [{}] {} 등록 완료", category.getDescription(), name);
    }
}
