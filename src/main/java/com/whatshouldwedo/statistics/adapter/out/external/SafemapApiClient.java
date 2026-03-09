package com.whatshouldwedo.statistics.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.config.PublicDataApiProperties;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class SafemapApiClient {

    @Value("${public-data.api.safemap.wfs-base-url}")
    private String wfsBaseUrl;

    @Value("${public-data.api.safemap.wms-base-url}")
    private String wmsBaseUrl;

    @Value("${public-data.api.safemap.wfs-crime-url}")
    private String wfsCrimeUrl;

    @Value("${public-data.api.safemap.wms-crime-url}")
    private String wmsCrimeUrl;

    private final PublicDataApiProperties apiProperties;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    /**
     * 범죄주의구간 WFS 전체 수집 후 시군구별 등급 분포로 집계
     * 결과: 시군구당 1건, data에 등급별 구간 수 포함
     */
    public List<StatisticsRecord> collectCrimeHotspots() {
        log.info("[safemap] 범죄주의구간 수집 시작 (WFS)");

        String serviceKey = apiProperties.getSafeMapGoKr().getKey();
        if (serviceKey.isBlank()) {
            log.warn("[safemap] API 키가 설정되지 않았습니다.");
            return List.of();
        }

        // 시군구코드 → 등급별 카운트 집계
        // key: "ctprvn_cd+sgg_cd", value: { grad -> count }
        Map<String, Map<Integer, Integer>> regionGradCounts = new HashMap<>();
        Map<String, String> regionCtprvnMap = new HashMap<>();

        int startIndex = 0;
        int count = 1000;
        int totalFeatures = -1;
        int rawCount = 0;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(64 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        while (true) {
            try {
                int currentStart = startIndex;

                String response = webClient.get()
                        .uri(wfsBaseUrl, uriBuilder -> uriBuilder
                                .queryParam("serviceKey", serviceKey)
                                .queryParam("service", "WFS")
                                .queryParam("version", "2.0.0")
                                .queryParam("request", "GetFeature")
                                .queryParam("typeNames", "safemap:A2SM_CRMNLHSPOT_TOT")
                                .queryParam("outputFormat", "application/json")
                                .queryParam("count", String.valueOf(count))
                                .queryParam("startIndex", String.valueOf(currentStart))
                                .queryParam("propertyName", "ctprvn_cd,sgg_cd,GRAD,TY_CD")
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) break;

                JsonNode root = objectMapper.readTree(response);

                if (totalFeatures < 0) {
                    totalFeatures = root.path("totalFeatures").asInt(0);
                    log.info("[safemap] 범죄주의구간 전체 건수: {}", totalFeatures);
                }

                JsonNode features = root.path("features");
                if (!features.isArray() || features.isEmpty()) break;

                for (JsonNode feature : features) {
                    JsonNode props = feature.path("properties");
                    String ctprvnCd = props.path("ctprvn_cd").asText("");
                    String sggCd = props.path("sgg_cd").asText("");
                    int grad = props.path("GRAD").asInt(0);

                    if (ctprvnCd.isBlank()) continue;

                    String regionCode = ctprvnCd + sggCd;
                    regionCtprvnMap.put(regionCode, ctprvnCd);
                    regionGradCounts
                            .computeIfAbsent(regionCode, k -> new HashMap<>())
                            .merge(grad, 1, Integer::sum);
                    rawCount++;
                }

                startIndex += features.size();
                if (startIndex >= totalFeatures) break;

                Thread.sleep(200);

            } catch (Exception e) {
                log.error("[safemap] 범죄주의구간 수집 실패 - startIndex={}, error={}", startIndex, e.getMessage(), e);
                break;
            }
        }

        // 시군구별 집계 결과를 StatisticsRecord로 변환
        List<StatisticsRecord> records = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, Integer>> entry : regionGradCounts.entrySet()) {
            String regionCode = entry.getKey();
            Map<Integer, Integer> gradCounts = entry.getValue();

            int totalSegments = gradCounts.values().stream().mapToInt(Integer::intValue).sum();
            int highRiskSegments = gradCounts.entrySet().stream()
                    .filter(e -> e.getKey() >= 7)
                    .mapToInt(Map.Entry::getValue)
                    .sum();

            Map<String, Object> data = new HashMap<>();
            data.put("source", "safemap");
            data.put("category_item", "범죄주의구간");
            data.put("wms_url", wmsBaseUrl);
            data.put("wms_layer", "A2SM_CRMNLHSPOT_TOT");
            data.put("total_segments", totalSegments);
            data.put("high_risk_segments", highRiskSegments);
            for (Map.Entry<Integer, Integer> gc : gradCounts.entrySet()) {
                data.put("grade_" + gc.getKey(), gc.getValue());
            }

            StatisticsRecord record = StatisticsRecord.create(
                    null, EStatisticsCategory.SAFETY, regionCode,
                    EAdminLevel.SIGUNGU, null, "2025", data);
            records.add(record);
        }

        log.info("[safemap] 범죄주의구간 집계 완료 - 원본 {}건 → 시군구 {}개", rawCount, records.size());
        return records;
    }

    /**
     * 치안사고통계(9대 범죄) WFS 수집 → 시군구별 합산 집계
     * IF_0074, 레이어: A2SM_CRMNLSTATS, 2,072건
     */
    public List<StatisticsRecord> collectCrimeStats() {
        log.info("[safemap] 치안사고통계 수집 시작 (WFS)");

        String serviceKey = apiProperties.getSafeMapGoKr().getKey();
        if (serviceKey.isBlank()) {
            log.warn("[safemap] API 키가 설정되지 않았습니다.");
            return List.of();
        }

        // 시군구코드 → 범죄 유형별 합산
        Map<String, int[]> regionCrimeCounts = new HashMap<>();
        // 0=murder, 1=brglr, 2=rape, 3=theft, 4=violn, 5=arson, 6=nrctc, 7=tmpt, 8=gamble, 9=tot, 10=관서수
        String[] crimeFields = {"murder", "brglr", "rape", "theft", "violn", "arson", "nrctc", "tmpt", "gamble", "tot"};

        int startIndex = 0;
        int count = 1000;
        int totalFeatures = -1;
        int rawCount = 0;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        while (true) {
            try {
                int currentStart = startIndex;

                String response = webClient.get()
                        .uri(wfsCrimeUrl,
                                uriBuilder -> uriBuilder
                                        .queryParam("serviceKey", serviceKey)
                                        .queryParam("service", "WFS")
                                        .queryParam("version", "2.0.0")
                                        .queryParam("request", "GetFeature")
                                        .queryParam("typeNames", "safemap:A2SM_CRMNLSTATS")
                                        .queryParam("outputFormat", "application/json")
                                        .queryParam("count", String.valueOf(count))
                                        .queryParam("startIndex", String.valueOf(currentStart))
                                        .queryParam("sortBy", "ctprvn_cd")
                                        .queryParam("propertyName", "ctprvn_cd,sgg_cd,murder,brglr,rape,theft,violn,arson,nrctc,tmpt,gamble,tot")
                                        .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) break;

                JsonNode root = objectMapper.readTree(response);

                if (totalFeatures < 0) {
                    totalFeatures = root.path("totalFeatures").asInt(0);
                    log.info("[safemap] 치안사고통계 전체 건수: {}", totalFeatures);
                }

                JsonNode features = root.path("features");
                if (!features.isArray() || features.isEmpty()) break;

                for (JsonNode feature : features) {
                    JsonNode props = feature.path("properties");
                    String ctprvnCd = props.path("ctprvn_cd").asText("");
                    String sggCd = props.path("sgg_cd").asText("");
                    if (ctprvnCd.isBlank()) continue;

                    String regionCode = sggCd.isBlank() ? ctprvnCd : sggCd;
                    int[] counts = regionCrimeCounts.computeIfAbsent(regionCode, k -> new int[11]);
                    for (int i = 0; i < crimeFields.length; i++) {
                        counts[i] += props.path(crimeFields[i]).asInt(0);
                    }
                    counts[10]++; // 관서 수
                    rawCount++;
                }

                startIndex += features.size();
                if (startIndex >= totalFeatures) break;

                Thread.sleep(200);

            } catch (Exception e) {
                log.error("[safemap] 치안사고통계 수집 실패 - startIndex={}, error={}", startIndex, e.getMessage(), e);
                break;
            }
        }

        List<StatisticsRecord> records = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : regionCrimeCounts.entrySet()) {
            String regionCode = entry.getKey();
            int[] counts = entry.getValue();

            Map<String, Object> data = new HashMap<>();
            data.put("source", "safemap");
            data.put("category_item", "치안사고통계");
            data.put("wms_url", wmsCrimeUrl);
            data.put("wms_layer", "A2SM_CRMNLSTATS");
            data.put("police_station_count", counts[10]);
            for (int i = 0; i < crimeFields.length; i++) {
                data.put(crimeFields[i], counts[i]);
            }

            StatisticsRecord record = StatisticsRecord.create(
                    null, EStatisticsCategory.SAFETY, regionCode,
                    EAdminLevel.SIGUNGU, null, "2025", data);
            records.add(record);
        }

        log.info("[safemap] 치안사고통계 집계 완료 - 원본 {}건 → 시군구 {}개", rawCount, records.size());
        return records;
    }
}
