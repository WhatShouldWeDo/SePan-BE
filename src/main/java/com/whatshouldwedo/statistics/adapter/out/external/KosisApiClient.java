package com.whatshouldwedo.statistics.adapter.out.external;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.config.PublicDataApiProperties;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KosisApiClient {

    @Value("${public-data.api.kosis.base-url}")
    private String baseUrl;

    private final PublicDataApiProperties apiProperties;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public List<StatisticsRecord> collectPopulationByAge(String dataYear) {
        log.info("[KOSIS] 연령·성별 인구 데이터 수집 시작 - year={}", dataYear);
        // DT_1B040A3: 시군구별 성별 인구수 (주민등록인구현황)
        return callKosisApi("101", "DT_1B040A3", "T20",
                Map.of("objL1", "ALL"),
                dataYear + "01", dataYear + "01", "M",
                EStatisticsCategory.VOTER_INFO, "연령·성별 인구",
                EAdminLevel.SIGUNGU);
    }

    public List<StatisticsRecord> collectMulticulturalHouseholds(String dataYear) {
        log.info("[KOSIS] 다문화가구 현황 데이터 수집 시작 - year={}", dataYear);
        // DT_110025_A045_A: 읍면동별 다문화가구 현황 (prdSe=A, objL2=A01(합계)로 40000셀 제한 회피)
        Map<String, String> params = new HashMap<>();
        params.put("objL1", "ALL");
        params.put("objL2", "A01");
        return callKosisApi("110", "DT_110025_A045_A", "T001",
                params,
                dataYear, dataYear, "A",
                EStatisticsCategory.VOTER_INFO, "다문화가구 현황",
                EAdminLevel.HJDONG);
    }

    public List<StatisticsRecord> collectVitalStatistics(String dataYear) {
        log.info("[KOSIS] 인구동태 데이터 수집 시작 - year={}", dataYear);
        // DT_1B8000K: 읍면동/성별/인구동태건수 (prdSe=A, objL2=0(계)으로 40000셀 제한 회피)
        Map<String, String> params = new HashMap<>();
        params.put("objL1", "ALL");
        params.put("objL2", "0");
        return callKosisApi("101", "DT_1B8000K", "T10+T20+T30+T40+T50",
                params,
                dataYear, dataYear, "A",
                EStatisticsCategory.VOTER_INFO, "인구동태(출생·사망·혼인·이혼)",
                EAdminLevel.HJDONG);
    }

    public List<StatisticsRecord> collectForeignResidents(String dataYear) {
        log.info("[KOSIS] 외국인주민 현황 데이터 수집 시작 - year={}", dataYear);
        // DT_110025_A033_A: 읍면동별 외국인 주민현황 (prdSe=A, objL2=15110AA000(합계)로 40000셀 제한 회피)
        Map<String, String> params = new HashMap<>();
        params.put("objL1", "ALL");
        params.put("objL2", "15110AA000");
        return callKosisApi("110", "DT_110025_A033_A", "T001",
                params,
                dataYear, dataYear, "A",
                EStatisticsCategory.VOTER_INFO, "외국인주민 현황",
                EAdminLevel.HJDONG);
    }

    // ========== 읍면동 단위 (전수조사) ==========

    /**
     * 읍면동별 총인구 (주민등록인구, DT_1B04005N)
     * objL2=0(계)으로 제한하면 40,000셀 이내로 전국 한 번에 호출 가능 (약 3,900건)
     */
    public List<StatisticsRecord> collectPopulationEmd(String dataYear) {
        log.info("[KOSIS] 읍면동별 총인구 수집 시작 - year={}", dataYear);
        return callKosisApi("101", "DT_1B04005N", "T2",
                Map.of("objL1", "ALL", "objL2", "0"),
                dataYear + "01", dataYear + "01", "M",
                EStatisticsCategory.VOTER_INFO, "읍면동별 총인구",
                EAdminLevel.HJDONG);
    }

    /**
     * 가구형태별 가구 및 가구원 - 읍면동 (인구주택총조사, DT_1GA0501)
     * 5년 주기(prdSe=F), objL2~8 빈값, newEstPrdCnt=1
     */
    public List<StatisticsRecord> collectHouseholdByType(String dataYear) {
        log.info("[KOSIS] 가구형태별 가구 및 가구원 수집 시작");
        // 40,000셀 제한: 20개 itmId × ~3,800 읍면동 = 76,000셀 초과
        // 2배치로 분할 (10개씩 → 38,000셀 이내)
        String itmId1 = "T110+T120+T121+T122+T210+T220+T221+T222+T310+T320";
        String itmId2 = "T321+T322+T410+T420+T421+T422+T510+T520+T521+T522";

        List<StatisticsRecord> batch1 = callKosisCensusApi("101", "DT_1GA0501", itmId1,
                EStatisticsCategory.VOTER_INFO, "가구형태별 가구 및 가구원",
                EAdminLevel.HJDONG);

        List<StatisticsRecord> batch2 = callKosisCensusApi("101", "DT_1GA0501", itmId2,
                EStatisticsCategory.VOTER_INFO, "가구형태별 가구 및 가구원",
                EAdminLevel.HJDONG);

        // 같은 regionCode의 data를 병합
        Map<String, StatisticsRecord> merged = new HashMap<>();
        for (StatisticsRecord r : batch1) {
            merged.put(r.getRegionCode(), r);
        }
        for (StatisticsRecord r : batch2) {
            StatisticsRecord existing = merged.get(r.getRegionCode());
            if (existing != null) {
                existing.getData().putAll(r.getData());
            } else {
                merged.put(r.getRegionCode(), r);
            }
        }

        log.info("[KOSIS] 가구형태별 가구 및 가구원 병합 완료 - {}건", merged.size());
        return new ArrayList<>(merged.values());
    }

    /**
     * 인구주택총조사용 KOSIS API 호출 (5년 주기, objL2~8 빈값)
     */
    private List<StatisticsRecord> callKosisCensusApi(
            String orgId, String tblId, String itmId,
            EStatisticsCategory category, String categoryItemName,
            EAdminLevel adminLevel) {

        String apiKey = apiProperties.getKosis().getKey();
        if (apiKey.isBlank()) {
            log.warn("[KOSIS] API 키가 설정되지 않았습니다.");
            return List.of();
        }

        try {
            ExchangeStrategies strategies = ExchangeStrategies.builder()
                    .codecs(c -> c.defaultCodecs().maxInMemorySize(32 * 1024 * 1024))
                    .build();
            WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

            String response = webClient.get()
                    .uri(baseUrl, uriBuilder -> uriBuilder
                            .queryParam("method", "getList")
                            .queryParam("apiKey", apiKey)
                            .queryParam("itmId", itmId)
                            .queryParam("objL1", "ALL")
                            .queryParam("objL2", "")
                            .queryParam("objL3", "")
                            .queryParam("objL4", "")
                            .queryParam("objL5", "")
                            .queryParam("objL6", "")
                            .queryParam("objL7", "")
                            .queryParam("objL8", "")
                            .queryParam("format", "json")
                            .queryParam("jsonVD", "Y")
                            .queryParam("prdSe", "F")
                            .queryParam("newEstPrdCnt", "1")
                            .queryParam("orgId", orgId)
                            .queryParam("tblId", tblId)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isBlank()) {
                log.warn("[KOSIS] 빈 응답 - tblId={}", tblId);
                return List.of();
            }

            return parseKosisResponse(response, category, categoryItemName, adminLevel, "census");

        } catch (Exception e) {
            log.error("[KOSIS] API 호출 실패 - tblId={}, error={}", tblId, e.getMessage(), e);
            return List.of();
        }
    }

    private List<StatisticsRecord> callKosisApi(
            String orgId, String tblId, String itmId,
            Map<String, String> objParams,
            String startPrdDe, String endPrdDe, String prdSe,
            EStatisticsCategory category, String categoryItemName,
            EAdminLevel adminLevel) {

        String apiKey = apiProperties.getKosis().getKey();
        if (apiKey.isBlank()) {
            log.warn("[KOSIS] API 키가 설정되지 않았습니다. 수집을 건너뜁니다.");
            return List.of();
        }

        try {
            ExchangeStrategies strategies = ExchangeStrategies.builder()
                    .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB
                    .build();
            WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

            String response = webClient.get()
                    .uri(baseUrl, uriBuilder -> {
                        uriBuilder
                                .queryParam("method", "getList")
                                .queryParam("apiKey", apiKey)
                                .queryParam("itmId", itmId)
                                .queryParam("prdSe", prdSe)
                                .queryParam("startPrdDe", startPrdDe)
                                .queryParam("endPrdDe", endPrdDe)
                                .queryParam("orgId", orgId)
                                .queryParam("tblId", tblId)
                                .queryParam("format", "json")
                                .queryParam("jsonVD", "Y");
                        // objL 파라미터는 존재하는 것만 추가 (빈값 전송 시 KOSIS에서 에러)
                        objParams.forEach(uriBuilder::queryParam);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isBlank()) {
                log.warn("[KOSIS] 빈 응답 수신 - orgId={}, tblId={}", orgId, tblId);
                return List.of();
            }

            return parseKosisResponse(response, category, categoryItemName, adminLevel, startPrdDe);

        } catch (Exception e) {
            log.error("[KOSIS] API 호출 실패 - orgId={}, tblId={}, error={}", orgId, tblId, e.getMessage(), e);
            return List.of();
        }
    }

    private List<StatisticsRecord> parseKosisResponse(
            String response, EStatisticsCategory category, String categoryItemName,
            EAdminLevel adminLevel, String dataYear) {

        List<StatisticsRecord> records = new ArrayList<>();
        try {
            // 1. 에러 응답 감지: KOSIS는 에러 시 JSON Object {"err":"21","errMsg":"..."}를 반환
            String trimmed = response.trim();
            if (trimmed.startsWith("{")) {
                JsonNode errorNode = objectMapper.readTree(trimmed);
                String errCode = errorNode.path("err").asText("");
                String errMsg = errorNode.path("errMsg").asText("");
                log.warn("[KOSIS] API 에러 응답 - category={}, err={}, errMsg={}",
                        categoryItemName, errCode, errMsg);
                return List.of();
            }

            // 2. 정상 응답: JSON Array
            List<Map<String, Object>> items = objectMapper.readValue(
                    response, new TypeReference<>() {});

            // 지역코드(C1)별로 그룹핑
            Map<String, Map<String, Object>> regionDataMap = new HashMap<>();

            for (Map<String, Object> item : items) {
                String regionCode = String.valueOf(item.getOrDefault("C1", ""));
                String regionName = String.valueOf(item.getOrDefault("C1_NM", ""));
                String itemName = String.valueOf(item.getOrDefault("ITM_NM", ""));
                String value = String.valueOf(item.getOrDefault("DT", ""));
                String unit = String.valueOf(item.getOrDefault("UNIT_NM", ""));

                if (regionCode.isBlank() || "00".equals(regionCode)) continue;

                Map<String, Object> data = regionDataMap.computeIfAbsent(regionCode, k -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("region_name", regionName);
                    m.put("unit", unit);
                    m.put("source", "KOSIS");
                    m.put("category_item", categoryItemName);
                    return m;
                });

                try {
                    String cleanValue = value.replace(",", "").replace("-", "0").trim();
                    if (!cleanValue.isEmpty()) {
                        data.put(normalizeFieldName(itemName), Double.parseDouble(cleanValue));
                    }
                } catch (NumberFormatException e) {
                    data.put(normalizeFieldName(itemName), value);
                }
            }

            // StatisticsRecord로 변환
            for (Map.Entry<String, Map<String, Object>> entry : regionDataMap.entrySet()) {
                StatisticsRecord record = StatisticsRecord.create(
                        null,
                        category,
                        entry.getKey(),
                        adminLevel,
                        null,
                        dataYear,
                        entry.getValue()
                );
                records.add(record);
            }

            log.info("[KOSIS] {} 데이터 파싱 완료 - {}건", categoryItemName, records.size());

        } catch (Exception e) {
            log.error("[KOSIS] 응답 파싱 실패 - category={}, error={}",
                    categoryItemName, e.getMessage(), e);
        }

        return records;
    }

    private String normalizeFieldName(String name) {
        return name.replaceAll("[\\s·()\\-/]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "")
                .toLowerCase();
    }
}
