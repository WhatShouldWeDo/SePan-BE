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
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LofinApiClient {

    @Value("${public-data.api.lofin.base-url}")
    private String baseUrl;

    private final PublicDataApiProperties apiProperties;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public List<StatisticsRecord> collectFinancialIndependence(String dataYear) {
        log.info("[LOFIN] 지방재정자립도 수집 - year={}", dataYear);

        String apiKey = apiProperties.getLofin().getKey();
        if (apiKey.isBlank()) {
            log.warn("[LOFIN] API 키가 설정되지 않았습니다.");
            return List.of();
        }

        List<StatisticsRecord> records = new ArrayList<>();
        int pageIndex = 1;
        int pageSize = 100;
        boolean hasMore = true;

        try {
            WebClient webClient = webClientBuilder.build();

            while (hasMore) {
                int currentPage = pageIndex;
                String response = webClient.get()
                        .uri(baseUrl, uriBuilder -> uriBuilder
                                .queryParam("Key", apiKey)
                                .queryParam("Type", "json")
                                .queryParam("pIndex", String.valueOf(currentPage))
                                .queryParam("pSize", String.valueOf(pageSize))
                                .queryParam("fyr", dataYear)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) {
                    log.warn("[LOFIN] 빈 응답");
                    break;
                }

                JsonNode root = objectMapper.readTree(response);
                JsonNode fncst = root.path("FNCST");

                if (!fncst.isArray() || fncst.size() < 2) {
                    String errorCode = fncst.path(0).path("head").path(0).path("RESULT").path("CODE").asText("");
                    if (errorCode.isEmpty()) {
                        errorCode = root.path("RESULT").path(0).path("CODE").asText("");
                    }
                    log.warn("[LOFIN] 응답 에러 - code={}", errorCode);
                    break;
                }

                int totalCount = fncst.path(0).path("head").path(0).path("list_total_count").asInt(0);
                JsonNode rows = fncst.path(1).path("row");

                if (rows.isMissingNode() || !rows.isArray()) {
                    break;
                }

                for (JsonNode row : rows) {
                    Map<String, Object> data = objectMapper.convertValue(row, new TypeReference<>() {});
                    String regionCode = String.valueOf(data.getOrDefault("laf_cd", ""));

                    data.put("source", "LOFIN");
                    data.put("category_item", "지방재정자립도");

                    StatisticsRecord record = StatisticsRecord.create(
                            null, EStatisticsCategory.ECONOMY, regionCode,
                            EAdminLevel.SIGUNGU, null, dataYear, data);
                    records.add(record);
                }

                if (records.size() >= totalCount) {
                    hasMore = false;
                } else {
                    pageIndex++;
                    Thread.sleep(100);
                }
            }

            log.info("[LOFIN] 지방재정자립도 수집 완료 - {}건", records.size());

        } catch (Exception e) {
            log.error("[LOFIN] API 호출 실패 - error={}", e.getMessage(), e);
        }

        return records;
    }
}
