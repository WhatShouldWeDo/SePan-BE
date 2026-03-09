package com.whatshouldwedo.statistics.adapter.out.external;

import com.whatshouldwedo.region.application.port.out.SidoRepository;
import com.whatshouldwedo.region.application.port.out.SigunguRepository;
import com.whatshouldwedo.region.domain.Sido;
import com.whatshouldwedo.statistics.application.port.out.PublicDataCollector;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static com.whatshouldwedo.statistics.domain.type.EStatisticsCategory.*;

@Configuration
public class CollectorRegistration {

    // ========== KOSIS 수집기 ==========

    @Bean
    PublicDataCollector populationByAgeCollector(KosisApiClient client) {
        return kosisCollector(VOTER_INFO, "연령·성별 인구", client::collectPopulationByAge);
    }

    @Bean
    PublicDataCollector multiculturalHouseholdsCollector(KosisApiClient client) {
        return kosisCollector(VOTER_INFO, "다문화가구 현황", client::collectMulticulturalHouseholds);
    }

    @Bean
    PublicDataCollector vitalStatisticsCollector(KosisApiClient client) {
        return kosisCollector(VOTER_INFO, "인구동태(출생·사망·혼인·이혼)", client::collectVitalStatistics);
    }

    @Bean
    PublicDataCollector foreignResidentsCollector(KosisApiClient client) {
        return kosisCollector(VOTER_INFO, "외국인주민 현황", client::collectForeignResidents);
    }

    // ========== KOSIS 읍면동 단위 수집기 ==========

    @Bean
    PublicDataCollector populationEmdCollector(KosisApiClient client) {
        return kosisCollector(VOTER_INFO, "읍면동별 총인구", client::collectPopulationEmd);
    }

    @Bean
    PublicDataCollector householdByTypeCollector(KosisApiClient client) {
        return new AbstractApiCollector(VOTER_INFO, "가구형태별 가구 및 가구원") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return client.collectHouseholdByType(dataYear);
            }

            @Override
            protected String getProviderName() {
                return "KOSIS";
            }
        };
    }

    // 읍면동별 다문화가구·외국인주민·인구동태 → KOSIS parametric API 지원 확인 완료 (prdSe=A)

    // ========== data.go.kr 수집기 (전국 시도 순회) ==========

    @Bean
    PublicDataCollector elderlyWelfareFacilitiesCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(WELFARE, "노인복지시설") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectElderlyWelfareFacilities();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    @Bean
    PublicDataCollector schoolInfoCollector(DataGoKrApiClient client) {
        return new AbstractApiCollector(EDUCATION, "초중등학교 기본정보") {
            private static final String[] EDU_CODES = {
                    "B10", "C10", "D10", "E10", "F10", "G10", "H10",
                    "I10", "J10", "K10", "M10", "N10", "P10", "Q10", "R10", "S10", "T10"
            };

            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                List<StatisticsRecord> all = new ArrayList<>();
                for (String eduCode : EDU_CODES) {
                    all.addAll(client.collectSchoolInfo(eduCode));
                    sleep(100);
                }
                return all;
            }

            @Override
            protected String getProviderName() {
                return "NEIS";
            }
        };
    }

    @Bean
    PublicDataCollector aptTradeCollector(DataGoKrApiClient client, SigunguRepository sigunguRepository,
                                          SidoRepository sidoRepository) {
        return new AbstractApiCollector(HOUSING, "아파트 매매 실거래가") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                List<StatisticsRecord> all = new ArrayList<>();
                for (Sido sido : sidoRepository.findAll()) {
                    var sigungus = sigunguRepository.findAllBySidoId(sido.getId());
                    for (var sigungu : sigungus) {
                        all.addAll(client.collectAptTrade(sigungu.getCode(), dataYear + "01"));
                        sleep(100);
                    }
                }
                return all;
            }

            @Override
            protected String getProviderName() {
                return "data.go.kr";
            }
        };
    }

    @Bean
    PublicDataCollector touristAttractionsCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(CULTURE, "관광지") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectTouristAttractions();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    @Bean
    PublicDataCollector busStopsCollector(DataGoKrApiClient client, SigunguRepository sigunguRepository,
                                          SidoRepository sidoRepository) {
        return new AbstractApiCollector(TRANSPORT, "버스정류소 현황") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                List<StatisticsRecord> all = new ArrayList<>();
                // TAGO API는 자체 도시코드 체계 사용 (tago_city_codes.csv)
                List<String> tagoCityCodes = loadTagoCityCodes();
                for (String cityCode : tagoCityCodes) {
                    all.addAll(client.collectBusStops(cityCode));
                    sleep(100);
                }
                return all;
            }

            private List<String> loadTagoCityCodes() {
                List<String> codes = new ArrayList<>();
                try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(
                        new org.springframework.core.io.ClassPathResource("data/tago_city_codes.csv")
                                .getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                    String line;
                    boolean isHeader = true;
                    while ((line = reader.readLine()) != null) {
                        if (isHeader) { isHeader = false; continue; }
                        String[] parts = line.split(",", -1);
                        if (parts.length >= 1 && !parts[0].isBlank()) {
                            codes.add(parts[0].trim());
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("TAGO 도시코드 로딩 실패: " + e.getMessage(), e);
                }
                return codes;
            }

            @Override
            protected String getProviderName() {
                return "data.go.kr";
            }
        };
    }

    // ========== 선거분석 수집기 ==========

    @Bean
    PublicDataCollector electionPledgesCollector(DataGoKrApiClient client) {
        return electionCollector("선거공약", client::collectElectionPledges);
    }

    @Bean
    PublicDataCollector partyPolicyCollector(DataGoKrApiClient client) {
        return new AbstractApiCollector(ELECTION_ANALYSIS, "정당정책") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                List<String> sgIds = resolveLocalElectionSgIds(dataYear);
                if (sgIds.isEmpty()) {
                    logInfo("[NEC] 정당정책 - {}년에 해당하는 지방선거 없음, 건너뜀", dataYear);
                    return List.of();
                }
                List<StatisticsRecord> all = new ArrayList<>();
                for (String sgId : sgIds) {
                    all.addAll(client.collectPartyPolicy(sgId, null));
                }
                return all;
            }

            @Override
            protected String getProviderName() {
                return "NEC";
            }
        };
    }

    @Bean
    PublicDataCollector voterCountCollector(DataGoKrApiClient client) {
        return electionCollector("선거인수", client::collectVoterCount);
    }

    @Bean
    PublicDataCollector candidateInfoCollector(DataGoKrApiClient client) {
        return electionCollector("후보자정보", client::collectCandidateInfo);
    }

    @Bean
    PublicDataCollector voteResultCollector(DataGoKrApiClient client) {
        return new AbstractApiCollector(ELECTION_ANALYSIS, "개표결과") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                List<String> sgIds = resolveLocalElectionSgIds(dataYear);
                if (sgIds.isEmpty()) {
                    logInfo("[NEC] 개표결과 - {}년에 해당하는 지방선거 없음, 건너뜀", dataYear);
                    return List.of();
                }
                List<StatisticsRecord> all = new ArrayList<>();
                for (String sgId : sgIds) {
                    all.addAll(client.collectVoteResult(sgId, null));
                }
                return all;
            }

            @Override
            protected String getProviderName() {
                return "NEC";
            }
        };
    }

    @Bean
    PublicDataCollector earlyVotingCollector(DataGoKrApiClient client) {
        return new AbstractApiCollector(ELECTION_ANALYSIS, "사전투표정보") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                List<String> sgIds = resolveLocalElectionSgIds(dataYear);
                if (sgIds.isEmpty()) {
                    logInfo("[NEC] 사전투표정보 - {}년에 해당하는 지방선거 없음, 건너뜀", dataYear);
                    return List.of();
                }
                List<StatisticsRecord> all = new ArrayList<>();
                for (String sgId : sgIds) {
                    all.addAll(client.collectEarlyVoting(sgId, null));
                }
                return all;
            }

            @Override
            protected String getProviderName() {
                return "NEC";
            }
        };
    }

    // ========== data.go.kr 추가 수집기 ==========

    @Bean
    PublicDataCollector aptComplexCollector(DataGoKrApiClient client, SigunguRepository sigunguRepository,
                                            SidoRepository sidoRepository) {
        return new AbstractApiCollector(HOUSING, "공동주택 단지 목록") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                List<StatisticsRecord> all = new ArrayList<>();
                for (Sido sido : sidoRepository.findAll()) {
                    var sigungus = sigunguRepository.findAllBySidoId(sido.getId());
                    for (var sigungu : sigungus) {
                        all.addAll(client.collectAptComplex(sigungu.getCode()));
                        sleep(100);
                    }
                }
                return all;
            }

            @Override
            protected String getProviderName() {
                return "data.go.kr";
            }
        };
    }

    @Bean
    PublicDataCollector aptRentCollector(DataGoKrApiClient client, SigunguRepository sigunguRepository,
                                         SidoRepository sidoRepository) {
        return new AbstractApiCollector(HOUSING, "아파트 전월세 실거래가") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                List<StatisticsRecord> all = new ArrayList<>();
                for (Sido sido : sidoRepository.findAll()) {
                    var sigungus = sigunguRepository.findAllBySidoId(sido.getId());
                    for (var sigungu : sigungus) {
                        all.addAll(client.collectAptRent(sigungu.getCode(), dataYear + "01"));
                        sleep(100);
                    }
                }
                return all;
            }

            @Override
            protected String getProviderName() {
                return "data.go.kr";
            }
        };
    }

    // TODO: 편의점(#18) - safemap LINK형 API 서비스 종료 상태. 대체 데이터 소스 확인 필요

    @Bean
    PublicDataCollector corporateFinanceCollector(DataGoKrApiClient client) {
        return new AbstractApiCollector(ECONOMY, "기업재무정보") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return client.collectCorporateFinance(dataYear);
            }

            @Override
            protected String getProviderName() {
                return "data.go.kr";
            }
        };
    }

    @Bean
    PublicDataCollector fireStationCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(SAFETY, "소방서 관할구역") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectFireStations();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    @Bean
    PublicDataCollector groupMealFacilitiesCollector(DataGoKrApiClient client) {
        return new AbstractApiCollector(WELFARE, "집단급식소") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return client.collectGroupMealFacilities();
            }

            @Override
            protected String getProviderName() {
                return "식약처";
            }
        };
    }

    // ========== V-World 추가 수집기 ==========

    @Bean
    PublicDataCollector childWelfareFacilitiesCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(WELFARE, "아동복지시설") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectChildWelfareFacilities();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    @Bean
    PublicDataCollector disasterRiskZonesCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(SAFETY, "재해위험지구") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectDisasterRiskZones();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    @Bean
    PublicDataCollector mainCommercialAreasCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(ECONOMY, "주요상권") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectMainCommercialAreas();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    @Bean
    PublicDataCollector dogParksCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(WELFARE, "반려견놀이터") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectDogParks();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    @Bean
    PublicDataCollector traditionalMarketCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(ECONOMY, "전통시장") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectTraditionalMarkets();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    @Bean
    PublicDataCollector urbanLandUseCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(HOUSING, "도시지역 용도") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectUrbanLandUse();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    @Bean
    PublicDataCollector nationalPlanZoneCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(HOUSING, "국토계획구역") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectNationalPlanZone();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    @Bean
    PublicDataCollector urbanPlanTransportCollector(VWorldApiClient vWorldClient) {
        return new AbstractApiCollector(TRANSPORT, "도시계획 교통시설") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return vWorldClient.collectUrbanPlanTransport();
            }

            @Override
            protected String getProviderName() {
                return "V-World";
            }
        };
    }

    // ========== safemap 수집기 ==========

    @Bean
    PublicDataCollector crimeHotspotsCollector(SafemapApiClient safemapClient) {
        return new AbstractApiCollector(SAFETY, "범죄주의구간") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return safemapClient.collectCrimeHotspots();
            }

            @Override
            protected String getProviderName() {
                return "safemap";
            }
        };
    }

    @Bean
    PublicDataCollector crimeStatsCollector(SafemapApiClient safemapClient) {
        return new AbstractApiCollector(SAFETY, "치안사고통계") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return safemapClient.collectCrimeStats();
            }

            @Override
            protected String getProviderName() {
                return "safemap";
            }
        };
    }

    // ========== 서울데이터 수집기 ==========

    @Bean
    PublicDataCollector constructionWorkCollector(SeoulDataApiClient seoulClient) {
        return new AbstractApiCollector(SAFETY, "건설사업 현황") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return seoulClient.collectConstructionWork();
            }

            @Override
            protected String getProviderName() {
                return "서울데이터";
            }
        };
    }

    // ========== 커리어넷 수집기 ==========

    @Bean
    PublicDataCollector careerNetSchoolCollector(CareerNetApiClient careerClient) {
        return new AbstractApiCollector(EDUCATION, "커리어넷 학교정보") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return careerClient.collectSchoolInfo();
            }

            @Override
            protected String getProviderName() {
                return "커리어넷";
            }
        };
    }

    // ========== LOFIN 수집기 ==========

    @Bean
    PublicDataCollector financialIndependenceCollector(LofinApiClient client) {
        return new AbstractApiCollector(ECONOMY, "지방재정자립도") {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return client.collectFinancialIndependence(dataYear);
            }

            @Override
            protected String getProviderName() {
                return "LOFIN";
            }
        };
    }

    // ========== 헬퍼 ==========

    private PublicDataCollector kosisCollector(EStatisticsCategory category, String itemName,
                                               java.util.function.Function<String, List<StatisticsRecord>> fn) {
        return new AbstractApiCollector(category, itemName) {
            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                return fn.apply(dataYear);
            }

            @Override
            protected String getProviderName() {
                return "KOSIS";
            }
        };
    }

    /**
     * dataYear에 해당하는 지방선거 sgId 목록 반환.
     * sgId는 선거일(yyyyMMdd) 형식이며, 해당 연도에 지방선거가 없으면 빈 리스트.
     * dataYear가 8자리(sgId 직접 지정)인 경우 그대로 사용.
     */
    private static List<String> resolveLocalElectionSgIds(String dataYear) {
        // dataYear가 이미 sgId 형식(8자리)이면 그대로 사용
        if (dataYear.length() == 8) {
            return List.of(dataYear);
        }
        // 연도별 전국동시지방선거 + 재보궐선거 매핑
        return switch (dataYear) {
            case "2022" -> List.of("20220601");  // 제8회 지방선거
            case "2023" -> List.of("20231011");  // 2023 재보궐선거 (공약 데이터 있음)
            case "2018" -> List.of("20180613");  // 제7회 지방선거
            case "2014" -> List.of("20140604");  // 제6회 지방선거
            case "2010" -> List.of("20100602");  // 제5회 지방선거
            default -> List.of();
        };
    }

    private PublicDataCollector electionCollector(String itemName,
                                                   java.util.function.BiFunction<String, String, List<StatisticsRecord>> fn) {
        return new AbstractApiCollector(ELECTION_ANALYSIS, itemName) {
            // sgTypecodes: 2=시도지사, 3=구시군장, 4=시도의원/구시군장(재보궐), 5=구시군의원, 6=비례시도의원, 8=비례구시군의원, 9=비례구시군의원2, 11=교육감
            private static final String[] SG_TYPECODES = {"2", "3", "4", "5", "6", "8", "9", "11"};

            @Override
            protected List<StatisticsRecord> doCollect(String dataYear) {
                List<String> sgIds = resolveLocalElectionSgIds(dataYear);
                if (sgIds.isEmpty()) {
                    logInfo("[NEC] {} - {}년에 해당하는 지방선거 없음, 건너뜀", itemName, dataYear);
                    return List.of();
                }
                List<StatisticsRecord> all = new ArrayList<>();
                for (String sgId : sgIds) {
                    for (String sgTypecode : SG_TYPECODES) {
                        all.addAll(fn.apply(sgId, sgTypecode));
                        sleep(100);
                    }
                }
                return all;
            }

            @Override
            protected String getProviderName() {
                return "NEC";
            }
        };
    }
}
