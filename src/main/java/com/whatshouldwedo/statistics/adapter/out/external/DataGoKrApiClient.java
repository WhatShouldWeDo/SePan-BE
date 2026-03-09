package com.whatshouldwedo.statistics.adapter.out.external;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.application.service.RegionCodeResolver;
import com.whatshouldwedo.statistics.config.PublicDataApiProperties;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataGoKrApiClient {

    private static final XmlMapper XML_MAPPER = new XmlMapper();

    private final PublicDataApiProperties apiProperties;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final RegionCodeResolver regionCodeResolver;

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
    @Value("${public-data.api.neis.school-info}")
    private String neisSchoolInfoApi;
    @Value("${public-data.api.apt.trade}")
    private String aptTradeApi;
    @Value("${public-data.api.apt.rent}")
    private String aptRentApi;
    @Value("${public-data.api.apt.complex}")
    private String aptComplexApi;
    @Value("${public-data.api.bus.stop}")
    private String busStopApi;
    @Value("${public-data.api.traffic.info}")
    private String trafficInfoApi;
    @Value("${public-data.api.corporate.finance}")
    private String corporateFinanceApi;
    @Value("${public-data.api.food-safety.cafeteria}")
    private String foodSafetyCafeteriaApi;
    // ========== 선거분석 (선관위 API - XML 응답) ==========

    public List<StatisticsRecord> collectElectionPledges(String sgId, String sgTypecode) {
        log.info("[NEC] 선거공약 수집 - sgId={}, sgTypecode={}", sgId, sgTypecode);

        // 1단계: 후보자정보 API에서 huboid(후보자ID) 목록 수집
        List<String> candidateIds = collectCandidateIds(sgId, sgTypecode);
        if (candidateIds.isEmpty()) {
            log.info("[NEC] 선거공약 - 후보자 없음 (sgId={}, sgTypecode={})", sgId, sgTypecode);
            return List.of();
        }

        // 2단계: 각 후보자별 공약 조회
        List<StatisticsRecord> allRecords = new ArrayList<>();
        for (String cnddtId : candidateIds) {
            List<StatisticsRecord> pledges = callNecXmlApi(
                    necElectionPledgeApi,
                    Map.of("sgId", sgId, "sgTypecode", sgTypecode, "cnddtId", cnddtId),
                    EStatisticsCategory.ELECTION_ANALYSIS, "선거공약", EAdminLevel.SIGUNGU, sgId);
            allRecords.addAll(pledges);
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        log.info("[NEC] 선거공약 수집 완료 - sgId={}, sgTypecode={}, {}건", sgId, sgTypecode, allRecords.size());
        return allRecords;
    }

    private List<String> collectCandidateIds(String sgId, String sgTypecode) {
        String serviceKey = apiProperties.getDataGoKr().getKey();
        List<String> ids = new ArrayList<>();
        int pageNo = 1;
        boolean hasMore = true;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        while (hasMore) {
            try {
                int currentPage = pageNo;
                String response = webClient.get()
                        .uri(necCandidateApi,
                                uriBuilder -> uriBuilder
                                        .queryParam("serviceKey", serviceKey)
                                        .queryParam("sgId", sgId)
                                        .queryParam("sgTypecode", sgTypecode)
                                        .queryParam("pageNo", String.valueOf(currentPage))
                                        .queryParam("numOfRows", "100")
                                        .build())
                        .header("User-Agent", "Mozilla/5.0 SePan")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) break;

                // huboid 추출
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("<huboid>([^<]+)</huboid>")
                        .matcher(response);
                int count = 0;
                while (matcher.find()) {
                    ids.add(matcher.group(1));
                    count++;
                }

                if (count < 100) {
                    hasMore = false;
                } else {
                    pageNo++;
                }
                Thread.sleep(100);
            } catch (Exception e) {
                log.warn("[NEC] 후보자ID 수집 실패 - sgId={}, error={}", sgId, e.getMessage());
                hasMore = false;
            }
        }
        return ids;
    }

    public List<StatisticsRecord> collectPartyPolicy(String sgId, String sgTypecode) {
        log.info("[NEC] 정당정책 수집 - sgId={}, sgTypecode={}", sgId, sgTypecode);
        String[] partyNames = {
                "국민의힘", "더불어민주당", "조국혁신당", "개혁신당", "진보당",
                "새로운미래", "기본소득당", "사회민주당", "정의당", "녹색정의당"
        };
        String serviceKey = apiProperties.getDataGoKr().getKey();
        List<StatisticsRecord> allRecords = new ArrayList<>();

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        for (String partyName : partyNames) {
            try {
                String response = webClient.get()
                        .uri(necPartyPolicyApi,
                                uriBuilder -> uriBuilder
                                        .queryParam("serviceKey", serviceKey)
                                        .queryParam("sgId", sgId)
                                        .queryParam("partyName", partyName)
                                        .build())
                        .header("User-Agent", "Mozilla/5.0 SePan")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) continue;

                List<Map<String, Object>> items = parseNecXmlResponse(response);
                for (Map<String, Object> item : items) {
                    Map<String, Object> data = new HashMap<>(item);
                    data.put("source", "NEC");
                    data.put("category_item", "정당정책");

                    // 정당정책은 정당 단위 데이터 — regionCode를 정당명으로 설정
                    StatisticsRecord record = StatisticsRecord.create(
                            null, EStatisticsCategory.ELECTION_ANALYSIS, partyName,
                            EAdminLevel.SIDO, null, sgId, data);
                    allRecords.add(record);
                }
                Thread.sleep(100);
            } catch (Exception e) {
                log.warn("[NEC] 정당정책 수집 실패 - partyName={}, error={}", partyName, e.getMessage());
            }
        }
        log.info("[NEC] 정당정책 수집 완료 - sgId={}, {}건", sgId, allRecords.size());
        return allRecords;
    }

    public List<StatisticsRecord> collectVoterCount(String sgId, String sgTypecode) {
        log.info("[NEC] 선거인수 수집 - sgId={}, sgTypecode={}", sgId, sgTypecode);
        return callNecXmlApi(
                necElectorCountApi,
                Map.of("sgId", sgId, "sgTypecode", sgTypecode),
                EStatisticsCategory.ELECTION_ANALYSIS, "선거인수", EAdminLevel.HJDONG, sgId);
    }

    // ========== 교육 ==========

    public List<StatisticsRecord> collectSchoolInfo(String atptOfcdcScCode) {
        log.info("[NEIS] 학교기본정보 수집 - 시도교육청코드={}", atptOfcdcScCode);
        String url = neisSchoolInfoApi;
        return callNeisApi(url, Map.of(
                "ATPT_OFCDC_SC_CODE", atptOfcdcScCode
        ), EStatisticsCategory.EDUCATION, "초중등학교 기본정보");
    }

    // ========== 주거 ==========

    public List<StatisticsRecord> collectAptTrade(String lawdCd, String dealYmd) {
        log.info("[data.go.kr] 아파트 매매 실거래가 수집 - lawdCd={}, dealYmd={}", lawdCd, dealYmd);
        String url = aptTradeApi;
        List<StatisticsRecord> records = callPaginatedXmlApi(url, Map.of(
                "LAWD_CD", lawdCd,
                "DEAL_YMD", dealYmd
        ), EStatisticsCategory.HOUSING, "아파트 매매 실거래가", EAdminLevel.HJDONG, dealYmd.substring(0, 4));
        return resolveAptRecordsToHjdong(records, lawdCd);
    }

    // ========== 교통 ==========

    public List<StatisticsRecord> collectBusStops(String cityCode) {
        log.info("[data.go.kr] 버스정류소 수집 - cityCode={}", cityCode);
        String url = busStopApi;
        List<StatisticsRecord> records = callPaginatedApi(url, Map.of(
                "cityCode", cityCode
        ), EStatisticsCategory.TRANSPORT, "버스정류소 현황", EAdminLevel.HJDONG, "2025", cityCode);
        return resolveBusStopRecordsToHjdong(records);
    }

    public List<StatisticsRecord> collectCandidateInfo(String sgId, String sgTypecode) {
        log.info("[NEC] 후보자정보 수집 - sgId={}, sgTypecode={}", sgId, sgTypecode);
        return callNecXmlApi(
                necCandidateApi,
                Map.of("sgId", sgId, "sgTypecode", sgTypecode),
                EStatisticsCategory.ELECTION_ANALYSIS, "후보자정보", EAdminLevel.SIGUNGU, sgId);
    }

    public List<StatisticsRecord> collectVoteResult(String sgId, String sgTypecode) {
        log.info("[NEC] 개표결과 수집 (odcloud 자동변환 API)");
        // 15025527 fileData → odcloud 자동변환 API (JSON)
        String url = necVoteResultApi;
        String serviceKey = apiProperties.getDataGoKr().getKey();
        if (serviceKey.isBlank()) {
            log.warn("[NEC] 서비스 키가 설정되지 않았습니다.");
            return List.of();
        }

        List<StatisticsRecord> allRecords = new ArrayList<>();
        int page = 1;
        int perPage = 1000;
        boolean hasMore = true;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        while (hasMore) {
            try {
                int currentPage = page;

                String response = webClient.get()
                        .uri(url, uriBuilder -> uriBuilder
                                .queryParam("serviceKey", serviceKey)
                                .queryParam("page", String.valueOf(currentPage))
                                .queryParam("perPage", String.valueOf(perPage))
                                .build())
                        .header("User-Agent", "Mozilla/5.0 SePan")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) break;

                JsonNode root = objectMapper.readTree(response);
                JsonNode dataArray = root.path("data");
                int totalCount = root.path("totalCount").asInt(0);

                if (!dataArray.isArray() || dataArray.isEmpty()) break;

                for (JsonNode item : dataArray) {
                    Map<String, Object> data = objectMapper.convertValue(item, new com.fasterxml.jackson.core.type.TypeReference<>() {});
                    String sido = String.valueOf(data.getOrDefault("시도명", ""));
                    if (sido.isBlank()) continue;

                    // "시도 시군구 읍면동" 형태로 regionCode 구성
                    String sigungu = String.valueOf(data.getOrDefault("구시군명", ""));
                    String emd = String.valueOf(data.getOrDefault("읍면동명", ""));

                    String regionCode;
                    EAdminLevel level;
                    if (!emd.isBlank() && !"null".equals(emd)) {
                        regionCode = sido + " " + sigungu + " " + emd;
                        level = EAdminLevel.HJDONG;
                    } else if (!sigungu.isBlank() && !"null".equals(sigungu)) {
                        regionCode = sido + " " + sigungu;
                        level = EAdminLevel.SIGUNGU;
                    } else {
                        regionCode = sido;
                        level = EAdminLevel.SIDO;
                    }

                    data.put("source", "NEC");
                    data.put("category_item", "개표결과");

                    StatisticsRecord record = StatisticsRecord.create(
                            null, EStatisticsCategory.ELECTION_ANALYSIS, regionCode,
                            level, null, sgId, data);
                    allRecords.add(record);
                }

                if (page * perPage >= totalCount) {
                    hasMore = false;
                } else {
                    page++;
                }
                Thread.sleep(100);
            } catch (Exception e) {
                log.error("[NEC] 개표결과 수집 실패 - page={}, error={}", page, e.getMessage(), e);
                hasMore = false;
            }
        }
        log.info("[NEC] 개표결과 수집 완료 - {}건", allRecords.size());
        return allRecords;
    }

    public List<StatisticsRecord> collectEarlyVoting(String sgId, String sgTypecode) {
        log.info("[NEC] 사전투표정보 수집 - sgId={}", sgId);
        // erVotingDiv: 0=전체, 1=1일차, 2=2일차 (필수 파라미터)
        return callNecXmlApi(
                necEarlyVotingApi,
                Map.of("sgId", sgId, "erVotingDiv", "0"),
                EStatisticsCategory.ELECTION_ANALYSIS, "사전투표정보", EAdminLevel.HJDONG, sgId);
    }

    // ========== 주거부동산 (추가) ==========

    public List<StatisticsRecord> collectAptComplex(String sigunguCode) {
        log.info("[data.go.kr] 공동주택 단지 목록 수집 - sigunguCode={}", sigunguCode);
        String url = aptComplexApi;
        List<StatisticsRecord> records = callPaginatedApi(url, Map.of(
                "sigunguCode", sigunguCode
        ), EStatisticsCategory.HOUSING, "공동주택 단지 목록", EAdminLevel.SIGUNGU, "2025", sigunguCode);
        return resolveAptComplexToHjdong(records);
    }

    public List<StatisticsRecord> collectAptRent(String lawdCd, String dealYmd) {
        log.info("[data.go.kr] 아파트 전월세 실거래가 수집 - lawdCd={}, dealYmd={}", lawdCd, dealYmd);
        String url = aptRentApi;
        List<StatisticsRecord> records = callPaginatedXmlApi(url, Map.of(
                "LAWD_CD", lawdCd,
                "DEAL_YMD", dealYmd
        ), EStatisticsCategory.HOUSING, "아파트 전월세 실거래가", EAdminLevel.HJDONG, dealYmd.substring(0, 4));
        return resolveAptRecordsToHjdong(records, lawdCd);
    }

    // ========== 교통 (추가) ==========

    public List<StatisticsRecord> collectTrafficInfo(String roadNo) {
        log.info("[data.go.kr] 교통소통정보 수집 - roadNo={}", roadNo);
        String url = trafficInfoApi;
        return callPaginatedApi(url, Map.of(
                "roadNo", roadNo
        ), EStatisticsCategory.TRANSPORT, "교통소통정보", EAdminLevel.SIGUNGU, "2025", roadNo);
    }

    public List<StatisticsRecord> collectCorporateFinance(String crno) {
        log.info("[data.go.kr] 기업재무정보 수집");
        String url = corporateFinanceApi;
        return callPaginatedApi(url, Map.of(
                "resultType", "json"
        ), EStatisticsCategory.ECONOMY, "기업재무정보", EAdminLevel.SIDO, "2025");
    }

    // ========== 복지 (추가) ==========

    public List<StatisticsRecord> collectGroupMealFacilities() {
        String foodSafetyKey = apiProperties.getFoodSafety().getKey();
        if (foodSafetyKey.isBlank()) {
            log.warn("[식약처] 집단급식소 현황 수집 - 식약처 API 키 미설정. 수집 건너뜀.");
            return List.of();
        }

        log.info("[식약처] 집단급식소 현황 수집 시작");
        List<StatisticsRecord> allRecords = new ArrayList<>();
        int startIdx = 1;
        int pageSize = 1000;
        boolean hasMore = true;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        while (hasMore) {
            try {
                int endIdx = startIdx + pageSize - 1;
                // 식약처 API: /api/{인증키}/{서비스명}/{타입}/{시작}/{끝}
                String url = String.format("%s/%s/I2750/json/%d/%d",
                        foodSafetyCafeteriaApi, foodSafetyKey, startIdx, endIdx);

                String response = webClient.get()
                        .uri(url)
                        .header("User-Agent", "Mozilla/5.0 SePan")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block(Duration.ofSeconds(30));

                if (response == null || response.isBlank() || response.contains("인증키가 유효하지 않습니다")) {
                    log.warn("[식약처] 인증키 유효하지 않음 또는 응답 없음. 수집 중단.");
                    break;
                }

                JsonNode root = objectMapper.readTree(response);
                JsonNode serviceNode = root.path("I2750");
                String resultCode = serviceNode.path("RESULT").path("CODE").asText("");
                if (!"INFO-000".equals(resultCode)) {
                    String resultMsg = serviceNode.path("RESULT").path("MSG").asText("");
                    log.warn("[식약처] API 에러 - code={}, msg={}", resultCode, resultMsg);
                    break;
                }

                int totalCount = serviceNode.path("total_count").asInt(0);
                JsonNode rows = serviceNode.path("row");
                if (!rows.isArray() || rows.isEmpty()) {
                    hasMore = false;
                    continue;
                }

                for (JsonNode row : rows) {
                    Map<String, Object> data = objectMapper.convertValue(row, new com.fasterxml.jackson.core.type.TypeReference<>() {});
                    String addr = String.valueOf(data.getOrDefault("ADDR", ""));
                    String regionCode = "";
                    if (!addr.isBlank()) {
                        String[] parts = addr.split("\\s+");
                        if (parts.length >= 2) regionCode = parts[0] + " " + parts[1];
                        else regionCode = parts[0];
                    }

                    data.put("source", "식약처");
                    data.put("category_item", "집단급식소");

                    StatisticsRecord record = StatisticsRecord.create(
                            null, EStatisticsCategory.WELFARE, regionCode,
                            EAdminLevel.SIGUNGU, null, "2025", data);
                    allRecords.add(record);
                }

                if (endIdx >= totalCount) {
                    hasMore = false;
                } else {
                    startIdx = endIdx + 1;
                }

                Thread.sleep(100);
            } catch (Exception e) {
                log.error("[식약처] 집단급식소 수집 실패 - startIdx={}, error={}", startIdx, e.getMessage());
                hasMore = false;
            }
        }

        log.info("[식약처] 집단급식소 수집 완료 - {}건", allRecords.size());
        return allRecords;
    }

    // ========== 공통 API 호출 ==========

    private List<StatisticsRecord> callPaginatedApi(
            String url, Map<String, String> params,
            EStatisticsCategory category, String categoryItemName,
            EAdminLevel adminLevel, String dataYear) {
        return callPaginatedApi(url, params, category, categoryItemName, adminLevel, dataYear, null);
    }

    private List<StatisticsRecord> callPaginatedApi(
            String url, Map<String, String> params,
            EStatisticsCategory category, String categoryItemName,
            EAdminLevel adminLevel, String dataYear, String defaultRegionCode) {

        String serviceKey = apiProperties.getDataGoKr().getKey();
        if (serviceKey.isBlank()) {
            log.warn("[data.go.kr] 서비스 키가 설정되지 않았습니다. 수집을 건너뜁니다.");
            return List.of();
        }

        List<StatisticsRecord> allRecords = new ArrayList<>();
        int pageNo = 1;
        int numOfRows = 100;
        boolean hasMore = true;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        while (hasMore) {
            try {
                int currentPage = pageNo;

                String response = webClient.get()
                        .uri(url, uriBuilder -> {
                            uriBuilder
                                    .queryParam("serviceKey", serviceKey)
                                    .queryParam("pageNo", String.valueOf(currentPage))
                                    .queryParam("numOfRows", String.valueOf(numOfRows))
                                    .queryParam("_type", "json");
                            params.forEach(uriBuilder::queryParam);
                            return uriBuilder.build();
                        })
                        .header("User-Agent", "Mozilla/5.0 SePan")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block(Duration.ofSeconds(30));

                if (response == null || response.isBlank()) {
                    break;
                }

                List<Map<String, Object>> items = parseDataGoKrResponse(response);
                if (items.isEmpty()) {
                    hasMore = false;
                } else {
                    for (Map<String, Object> item : items) {
                        String regionCode = extractRegionCode(item);
                        if (regionCode == null || regionCode.isBlank()) {
                            regionCode = defaultRegionCode;
                        }
                        if (regionCode == null || regionCode.isBlank()) continue;

                        Map<String, Object> data = new HashMap<>(item);
                        data.put("source", "data.go.kr");
                        data.put("category_item", categoryItemName);

                        StatisticsRecord record = StatisticsRecord.create(
                                null, category, regionCode, adminLevel, null, dataYear, data);
                        allRecords.add(record);
                    }

                    if (items.size() < numOfRows) {
                        hasMore = false;
                    } else {
                        pageNo++;
                    }
                }

                // 공공데이터 API 호출 제한을 위한 딜레이
                Thread.sleep(100);

            } catch (Exception e) {
                log.error("[data.go.kr] API 호출 실패 - url={}, page={}, error={}",
                        url, pageNo, e.getMessage(), e);
                hasMore = false;
            }
        }

        log.info("[data.go.kr] {} 수집 완료 - {}건", categoryItemName, allRecords.size());
        return allRecords;
    }

    /**
     * XML 응답을 반환하는 data.go.kr API 호출 (아파트 매매/전월세 등).
     * _type=json 파라미터를 추가하지 않고 XML을 직접 파싱한다.
     */
    private List<StatisticsRecord> callPaginatedXmlApi(
            String url, Map<String, String> params,
            EStatisticsCategory category, String categoryItemName,
            EAdminLevel adminLevel, String dataYear) {

        String serviceKey = apiProperties.getDataGoKr().getKey();
        if (serviceKey.isBlank()) {
            log.warn("[data.go.kr] 서비스 키가 설정되지 않았습니다. 수집을 건너뜁니다.");
            return List.of();
        }

        List<StatisticsRecord> allRecords = new ArrayList<>();
        int pageNo = 1;
        int numOfRows = 100;
        boolean hasMore = true;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        XmlMapper xmlMapper = XML_MAPPER;

        while (hasMore) {
            try {
                int currentPage = pageNo;

                String response = webClient.get()
                        .uri(url, uriBuilder -> {
                            uriBuilder
                                    .queryParam("serviceKey", serviceKey)
                                    .queryParam("pageNo", String.valueOf(currentPage))
                                    .queryParam("numOfRows", String.valueOf(numOfRows));
                            params.forEach(uriBuilder::queryParam);
                            return uriBuilder.build();
                        })
                        .header("User-Agent", "Mozilla/5.0 SePan")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) break;

                // XML 파싱
                JsonNode root = xmlMapper.readTree(response);
                String resultCode = root.path("header").path("resultCode").asText("");
                if (!"000".equals(resultCode) && !"00".equals(resultCode)) {
                    String resultMsg = root.path("header").path("resultMsg").asText("");
                    log.warn("[data.go.kr] XML API 에러 - code={}, msg={}", resultCode, resultMsg);
                    break;
                }

                JsonNode body = root.path("body");
                int totalCount = body.path("totalCount").asInt(0);
                JsonNode items = body.path("items").path("item");

                if (items.isMissingNode()) {
                    hasMore = false;
                    continue;
                }

                // 단일 item인 경우 배열로 변환
                List<Map<String, Object>> itemList;
                if (items.isArray()) {
                    itemList = objectMapper.convertValue(items, new TypeReference<>() {});
                } else if (items.isObject()) {
                    Map<String, Object> single = objectMapper.convertValue(items, new TypeReference<>() {});
                    itemList = List.of(single);
                } else {
                    hasMore = false;
                    continue;
                }

                for (Map<String, Object> item : itemList) {
                    String regionCode = extractRegionCode(item);
                    if (regionCode == null || regionCode.isBlank()) continue;

                    Map<String, Object> data = new HashMap<>(item);
                    data.put("source", "data.go.kr");
                    data.put("category_item", categoryItemName);

                    StatisticsRecord record = StatisticsRecord.create(
                            null, category, regionCode, adminLevel, null, dataYear, data);
                    allRecords.add(record);
                }

                if (pageNo * numOfRows >= totalCount) {
                    hasMore = false;
                } else {
                    pageNo++;
                }

                Thread.sleep(100);

            } catch (Exception e) {
                log.error("[data.go.kr] XML API 호출 실패 - url={}, page={}, error={}",
                        url, pageNo, e.getMessage(), e);
                hasMore = false;
            }
        }

        log.info("[data.go.kr] {} 수집 완료 - {}건", categoryItemName, allRecords.size());
        return allRecords;
    }

    private List<StatisticsRecord> callNeisApi(
            String url, Map<String, String> params,
            EStatisticsCategory category, String categoryItemName) {

        String neisKey = apiProperties.getNeisGoKr().getKey();
        if (neisKey.isBlank()) {
            log.warn("[NEIS] API 키가 설정되지 않았습니다. (public-data.neis-go-kr.key)");
            return List.of();
        }

        List<StatisticsRecord> records = new ArrayList<>();
        int pIndex = 1;
        int pSize = 100;
        boolean hasMore = true;

        ExchangeStrategies neisStrategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(neisStrategies).build();

        while (hasMore) {
            try {
                int currentIndex = pIndex;

                String response = webClient.get()
                        .uri(url, uriBuilder -> {
                            uriBuilder
                                    .queryParam("KEY", neisKey)
                                    .queryParam("Type", "json")
                                    .queryParam("pIndex", String.valueOf(currentIndex))
                                    .queryParam("pSize", String.valueOf(pSize));
                            params.forEach(uriBuilder::queryParam);
                            return uriBuilder.build();
                        })
                        .header("User-Agent", "Mozilla/5.0 SePan")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) break;

                JsonNode root = objectMapper.readTree(response);
                JsonNode schoolInfo = root.path("schoolInfo");
                if (schoolInfo.isMissingNode() || !schoolInfo.isArray() || schoolInfo.size() < 2) {
                    hasMore = false;
                    continue;
                }

                JsonNode rows = schoolInfo.get(1).path("row");
                if (rows.isMissingNode() || !rows.isArray() || rows.isEmpty()) {
                    hasMore = false;
                    continue;
                }

                for (JsonNode row : rows) {
                    Map<String, Object> data = objectMapper.convertValue(row, new TypeReference<>() {});

                    // 도로명주소 + 상세주소에서 행정동 추출
                    String orgRdnma = String.valueOf(data.getOrDefault("ORG_RDNMA", ""));
                    String orgRdnda = String.valueOf(data.getOrDefault("ORG_RDNDA", ""));
                    String regionCode = "";
                    EAdminLevel level = EAdminLevel.SIGUNGU;

                    // 도로명주소 + 상세(괄호 안 동명) 조합하여 행정동 추출
                    String fullAddress = orgRdnma;
                    if (!orgRdnda.isBlank() && !"null".equals(orgRdnda)) {
                        fullAddress = orgRdnma + " " + orgRdnda;
                    }

                    var resolved = regionCodeResolver.resolveFromAddress(fullAddress);
                    if (resolved != null && resolved.adminLevel() == EAdminLevel.HJDONG) {
                        regionCode = resolved.code();
                        level = EAdminLevel.HJDONG;
                    } else if (!orgRdnma.isBlank() && !"null".equals(orgRdnma)) {
                        // fallback 1: VWorld 도로명주소 지오코딩
                        // VWorld rate limit 방지: 50ms 간격
                        try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                        String hjdongCode = regionCodeResolver.geocodeAddress(orgRdnma);
                        if (hjdongCode != null) {
                            regionCode = hjdongCode;
                            level = EAdminLevel.HJDONG;
                        } else {
                            // fallback 2: ORG_RDNDA에서 동/읍/면명 추출 → "시도 시군구 동명" 형태로 parcel 검색
                            String dongName = regionCodeResolver.extractDongFromDetail(orgRdnda);
                            if (dongName != null) {
                                String[] addrParts = orgRdnma.split("\\s+");
                                // 시도 시군구 (+ 구) 동명 조합
                                String parcelAddr = buildParcelAddress(addrParts, dongName);
                                if (parcelAddr != null) {
                                    try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                                    hjdongCode = regionCodeResolver.geocodeAddress(parcelAddr);
                                    if (hjdongCode != null) {
                                        regionCode = hjdongCode;
                                        level = EAdminLevel.HJDONG;
                                    }
                                }
                            }
                            // 최종 fallback: 시도 시군구만 추출
                            if (level != EAdminLevel.HJDONG) {
                                String[] addrParts = orgRdnma.split("\\s+");
                                if (addrParts.length >= 2) {
                                    regionCode = addrParts[0] + " " + addrParts[1];
                                } else {
                                    regionCode = orgRdnma;
                                }
                            }
                        }
                    } else {
                        regionCode = String.valueOf(data.getOrDefault("LCTN_SC_NM", ""));
                    }

                    data.put("source", "NEIS");
                    data.put("category_item", categoryItemName);

                    StatisticsRecord record = StatisticsRecord.create(
                            null, category, regionCode, level, null, "2025", data);
                    records.add(record);
                }

                if (rows.size() < pSize) {
                    hasMore = false;
                } else {
                    pIndex++;
                }

                Thread.sleep(100);

            } catch (Exception e) {
                log.error("[NEIS] API 호출 실패 - error={}", e.getMessage(), e);
                hasMore = false;
            }
        }

        log.info("[NEIS] {} 수집 완료 - {}건", categoryItemName, records.size());
        return records;
    }

    /**
     * 도로명주소 파츠 + 동명으로 지번주소(parcel) 형태 구성.
     * 예: ["경기도", "용인시", "처인구", "고림로165번길"] + "고림동" → "경기도 용인시 처인구 고림동"
     */
    private String buildParcelAddress(String[] addrParts, String dongName) {
        if (addrParts.length < 2) return null;
        StringBuilder sb = new StringBuilder();
        // 시도
        sb.append(addrParts[0]);
        // 시군구 (+ 구)
        for (int i = 1; i < addrParts.length; i++) {
            String part = addrParts[i];
            if (part.endsWith("시") || part.endsWith("군") || part.endsWith("구")) {
                sb.append(" ").append(part);
            } else {
                break; // 도로명 등 비시군구 부분부터는 중단
            }
        }
        sb.append(" ").append(dongName);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseDataGoKrResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);

            // JSON 형식 응답 파싱
            JsonNode body = root.path("response").path("body");
            if (body.isMissingNode()) {
                // 중첩 구조가 다른 경우 시도
                body = root.path("body");
            }
            if (body.isMissingNode()) return List.of();

            JsonNode items = body.path("items").path("item");
            if (items.isMissingNode()) {
                items = body.path("items");
            }
            if (items.isMissingNode() || !items.isArray()) return List.of();

            return objectMapper.convertValue(items, new TypeReference<>() {});

        } catch (Exception e) {
            log.warn("[data.go.kr] 응답 파싱 실패: {}", e.getMessage());
            return List.of();
        }
    }

    // ========== 선관위 XML API 호출 ==========

    private List<StatisticsRecord> callNecXmlApi(
            String url, Map<String, String> params,
            EStatisticsCategory category, String categoryItemName,
            EAdminLevel adminLevel, String dataYear) {

        String serviceKey = apiProperties.getDataGoKr().getKey();
        if (serviceKey.isBlank()) {
            log.warn("[NEC] 서비스 키가 설정되지 않았습니다.");
            return List.of();
        }

        List<StatisticsRecord> allRecords = new ArrayList<>();
        int pageNo = 1;
        int numOfRows = 100;
        boolean hasMore = true;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        while (hasMore) {
            try {
                int currentPage = pageNo;

                String response = webClient.get()
                        .uri(url, uriBuilder -> {
                            uriBuilder
                                    .queryParam("serviceKey", serviceKey)
                                    .queryParam("pageNo", String.valueOf(currentPage))
                                    .queryParam("numOfRows", String.valueOf(numOfRows));
                            params.forEach(uriBuilder::queryParam);
                            return uriBuilder.build();
                        })
                        .header("User-Agent", "Mozilla/5.0 SePan")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) break;

                List<Map<String, Object>> items = parseNecXmlResponse(response);
                if (items.isEmpty()) {
                    hasMore = false;
                } else {
                    for (Map<String, Object> item : items) {
                        String regionCode = buildNecRegionCode(item);
                        if (regionCode.isBlank()) continue;

                        Map<String, Object> data = new HashMap<>(item);
                        data.put("source", "NEC");
                        data.put("category_item", categoryItemName);

                        // emdName이 있으면 HJDONG 레벨, wiwName만 있으면 SIGUNGU
                        EAdminLevel resolvedLevel = adminLevel;
                        String emdName = String.valueOf(item.getOrDefault("emdName", ""));
                        if (!emdName.isBlank() && !"null".equals(emdName)) {
                            resolvedLevel = EAdminLevel.HJDONG;
                        } else {
                            String wiwName = String.valueOf(item.getOrDefault("wiwName", ""));
                            if (!wiwName.isBlank() && !"null".equals(wiwName)) {
                                resolvedLevel = EAdminLevel.SIGUNGU;
                            }
                        }

                        StatisticsRecord record = StatisticsRecord.create(
                                null, category, regionCode, resolvedLevel, null, dataYear, data);
                        allRecords.add(record);
                    }

                    if (items.size() < numOfRows) {
                        hasMore = false;
                    } else {
                        pageNo++;
                    }
                }

                Thread.sleep(100);

            } catch (Exception e) {
                log.error("[NEC] API 호출 실패 - url={}, page={}, error={}", url, pageNo, e.getMessage(), e);
                hasMore = false;
            }
        }

        log.info("[NEC] {} 수집 완료 - {}건", categoryItemName, allRecords.size());
        return allRecords;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseNecXmlResponse(String response) {
        try {
            XmlMapper xmlMapper = XML_MAPPER;
            JsonNode root = xmlMapper.readTree(response);

            // 에러 체크
            String resultCode = root.path("header").path("resultCode").asText("");
            if (!"INFO-00".equals(resultCode) && !"00".equals(resultCode)) {
                String resultMsg = root.path("header").path("resultMsg").asText("");
                log.warn("[NEC] API 에러 - code={}, msg={}", resultCode, resultMsg);
                return List.of();
            }

            JsonNode items = root.path("body").path("items").path("item");
            if (items.isMissingNode()) {
                items = root.path("body").path("items");
            }
            if (items.isMissingNode()) return List.of();

            if (items.isArray()) {
                return objectMapper.convertValue(items, new TypeReference<>() {});
            } else if (items.isObject()) {
                // 단일 item인 경우
                Map<String, Object> single = objectMapper.convertValue(items, new TypeReference<>() {});
                return List.of(single);
            }

            return List.of();
        } catch (Exception e) {
            log.warn("[NEC] XML 파싱 실패: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * NEC 선관위 XML 응답에서 "시도 시군구 읍면동" 형태의 regionCode 구성
     */
    private String buildNecRegionCode(Map<String, Object> item) {
        String sdName = String.valueOf(item.getOrDefault("sdName", ""));
        // 공약 API는 sidoName 필드를 사용
        if (sdName.isBlank() || "null".equals(sdName)) {
            sdName = String.valueOf(item.getOrDefault("sidoName", ""));
        }
        String wiwName = String.valueOf(item.getOrDefault("wiwName", ""));
        String emdName = String.valueOf(item.getOrDefault("emdName", ""));

        if (sdName.isBlank() || "null".equals(sdName)) return "";

        StringBuilder sb = new StringBuilder(sdName);
        if (!wiwName.isBlank() && !"null".equals(wiwName) && !"합계".equals(wiwName)) {
            sb.append(" ").append(wiwName);
            if (!emdName.isBlank() && !"null".equals(emdName) && !"합계".equals(emdName)) {
                sb.append(" ").append(emdName);
            }
        }
        return sb.toString();
    }

    private String extractRegionCode(Map<String, Object> item) {
        // 국토부 API - LAWD_CD(법정동코드)
        if (item.containsKey("LAWD_CD")) {
            return String.valueOf(item.get("LAWD_CD"));
        }
        // 국토부 아파트 실거래가 XML - sggCd(시군구코드 5자리)
        if (item.containsKey("sggCd")) {
            return String.valueOf(item.get("sggCd"));
        }
        // 주소기반 - addr 필드
        if (item.containsKey("addr")) {
            return String.valueOf(item.get("addr"));
        }
        // 시도/시군구 코드
        if (item.containsKey("sidoCode")) {
            return String.valueOf(item.get("sidoCode"));
        }
        if (item.containsKey("sigunguCode")) {
            return String.valueOf(item.get("sigunguCode"));
        }
        // 기업재무정보 - crno(법인등록번호) 앞 2자리가 시도코드
        if (item.containsKey("crno")) {
            String crno = String.valueOf(item.get("crno"));
            if (crno.length() >= 2) {
                return crno.substring(0, 2);
            }
        }
        return null;
    }

    /**
     * 아파트 매매/전월세 레코드를 행정동으로 업그레이드.
     * 응답의 umdNm(법정동명) + 시군구코드 → dongNameToCode로 행정동코드 매핑.
     */
    private List<StatisticsRecord> resolveAptRecordsToHjdong(List<StatisticsRecord> records, String defaultSigunguCode) {
        List<StatisticsRecord> result = new ArrayList<>(records.size());
        int hjdongCount = 0;
        for (StatisticsRecord r : records) {
            Map<String, Object> data = r.getData();
            String sggCd = toStr(data, "sggCd");
            if (sggCd.isBlank()) sggCd = defaultSigunguCode;
            String umdCd = toStr(data, "umdCd");

            // 1) sggCd(5자리) + umdCd(5자리) = 법정동코드(10자리) → bjdongToHjdongMap 변환
            if (!sggCd.isBlank() && !umdCd.isBlank()) {
                String bjdongCode = sggCd + umdCd;
                var normalized = regionCodeResolver.normalize(bjdongCode, EAdminLevel.HJDONG);
                if (normalized.adminLevel() == EAdminLevel.HJDONG) {
                    result.add(StatisticsRecord.create(
                            r.getDatasetId(), r.getCategoryCode(), normalized.code(),
                            EAdminLevel.HJDONG, r.getHjdongVersionId(), r.getDataYear(), data));
                    hjdongCount++;
                    continue;
                }
            }

            // 2) fallback: dongName으로 findHjdongByDongName 시도
            String dongName = toStr(data, "umdNm");
            if (dongName.isBlank()) dongName = toStr(data, "법정동");
            // umdNm에 공백이 있으면 ("물금읍 범어리") 첫 토큰만 사용
            if (dongName.contains(" ")) {
                dongName = dongName.split("\\s+")[0];
            }

            if (!dongName.isBlank()) {
                String hjdongCode = regionCodeResolver.findHjdongByDongName(sggCd, dongName);
                if (hjdongCode != null) {
                    result.add(StatisticsRecord.create(
                            r.getDatasetId(), r.getCategoryCode(), hjdongCode,
                            EAdminLevel.HJDONG, r.getHjdongVersionId(), r.getDataYear(), data));
                    hjdongCount++;
                    continue;
                }
            }

            // 3) fallback: 시군구 코드 유지
            result.add(StatisticsRecord.create(
                    r.getDatasetId(), r.getCategoryCode(), sggCd,
                    EAdminLevel.SIGUNGU, r.getHjdongVersionId(), r.getDataYear(), data));
        }
        log.info("[data.go.kr] 아파트 HJDONG 변환 결과 - {}/{}건 성공", hjdongCount, records.size());
        return result;
    }

    /**
     * 버스정류소 레코드를 좌표 기반 역지오코딩으로 행정동 업그레이드.
     */
    private List<StatisticsRecord> resolveBusStopRecordsToHjdong(List<StatisticsRecord> records) {
        List<StatisticsRecord> result = new ArrayList<>(records.size());
        int geocoded = 0;
        for (int i = 0; i < records.size(); i++) {
            StatisticsRecord r = records.get(i);
            Map<String, Object> data = r.getData();
            double lat = toDouble(data, "gpslati");
            double lon = toDouble(data, "gpslong");

            if (lat != 0 && lon != 0) {
                // VWorld rate limit 방지: 매 호출 전 50ms 대기
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                String hjdongCode = regionCodeResolver.reverseGeocode(lon, lat);
                if (hjdongCode != null) {
                    result.add(StatisticsRecord.create(
                            r.getDatasetId(), r.getCategoryCode(), hjdongCode,
                            EAdminLevel.HJDONG, r.getHjdongVersionId(), r.getDataYear(), data));
                    geocoded++;
                    continue;
                }
            }
            // fallback: 원본 유지
            result.add(r);

            if (i > 0 && i % 10000 == 0) {
                log.info("[data.go.kr] 버스정류소 역지오코딩 진행 - {}/{}, HJDONG={}", i, records.size(), geocoded);
            }
        }
        log.info("[data.go.kr] 버스정류소 역지오코딩 결과 - {}/{}건 HJDONG 성공", geocoded, records.size());
        return result;
    }

    /**
     * 공동주택 단지 레코드를 as1(시도)+as2(시군구)+as3(동) 주소 기반으로 행정동 업그레이드.
     */
    private List<StatisticsRecord> resolveAptComplexToHjdong(List<StatisticsRecord> records) {
        List<StatisticsRecord> result = new ArrayList<>(records.size());
        int resolved = 0;
        for (StatisticsRecord r : records) {
            Map<String, Object> data = r.getData();
            String as1 = toStr(data, "as1"); // 시도
            String as2 = toStr(data, "as2"); // 시군구
            String as3 = toStr(data, "as3"); // 동/읍/면
            String as4 = toStr(data, "as4"); // 리 (nullable)

            // 1) 주소 조합으로 resolveFromAddress 시도
            String address = (as1 + " " + as2 + " " + as3).trim();
            if (!as4.isBlank()) {
                address += " " + as4;
            }

            String hjdongCode = null;
            if (!address.isBlank() && address.split("\\s+").length >= 3) {
                var resolved2 = regionCodeResolver.resolveFromAddress(address);
                if (resolved2 != null && resolved2.adminLevel() == EAdminLevel.HJDONG) {
                    hjdongCode = resolved2.code();
                }
            }

            // 2) VWorld 지오코딩 fallback
            if (hjdongCode == null && !address.isBlank()) {
                hjdongCode = regionCodeResolver.geocodeAddress(address);
            }

            if (hjdongCode != null) {
                result.add(StatisticsRecord.create(
                        r.getDatasetId(), r.getCategoryCode(), hjdongCode,
                        EAdminLevel.HJDONG, r.getHjdongVersionId(), r.getDataYear(), data));
                resolved++;
            } else {
                result.add(r);
            }
        }
        log.info("[data.go.kr] 공동주택 단지 HJDONG 변환 결과 - {}/{}건 성공", resolved, records.size());
        return result;
    }

    private String toStr(Map<String, Object> data, String key) {
        Object val = data.get(key);
        if (val == null) return "";
        String s = String.valueOf(val);
        return "null".equals(s) ? "" : s.trim();
    }

    private double toDouble(Map<String, Object> data, String key) {
        Object val = data.get(key);
        if (val == null) return 0;
        try {
            return Double.parseDouble(String.valueOf(val));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
