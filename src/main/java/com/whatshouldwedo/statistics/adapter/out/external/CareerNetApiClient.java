package com.whatshouldwedo.statistics.adapter.out.external;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CareerNetApiClient {

    @Value("${public-data.api.careernet.base-url}")
    private String baseUrl;

    private final PublicDataApiProperties apiProperties;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final RegionCodeResolver regionCodeResolver;

    public List<StatisticsRecord> collectSchoolInfo() {
        log.info("[커리어넷] 학교정보 수집 시작");

        String apiKey = apiProperties.getCareerNet().getKey();
        if (apiKey.isBlank()) {
            log.warn("[커리어넷] API 키가 설정되지 않았습니다.");
            return List.of();
        }

        List<StatisticsRecord> allRecords = new ArrayList<>();
        String[] gubuns = {"univ_list"}; // 대학교만 수집 (초중고는 NEIS에서 수집)

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        for (String gubun : gubuns) {
            int page = 1;
            boolean hasMore = true;

            while (hasMore) {
                try {
                    int currentPage = page;

                    String response = webClient.get()
                            .uri(baseUrl, uriBuilder -> uriBuilder
                                    .queryParam("apiKey", apiKey)
                                    .queryParam("svcType", "api")
                                    .queryParam("svcCode", "SCHOOL")
                                    .queryParam("contentType", "json")
                                    .queryParam("gubun", gubun)
                                    .queryParam("thisPage", String.valueOf(currentPage))
                                    .queryParam("perPage", "100")
                                    .build())
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

                    if (response == null || response.isBlank()) break;

                    JsonNode root = objectMapper.readTree(response);
                    JsonNode content = root.path("dataSearch").path("content");

                    if (!content.isArray() || content.isEmpty()) {
                        hasMore = false;
                        continue;
                    }

                    for (JsonNode item : content) {
                        Map<String, Object> data = objectMapper.convertValue(item, new TypeReference<>() {});

                        data.put("source", "커리어넷");
                        data.put("category_item", "커리어넷 학교정보");
                        data.put("gubun", gubun);

                        // adres(주소) 필드에서 행정동 추출 시도
                        String regionCode;
                        EAdminLevel level = EAdminLevel.SIGUNGU;

                        String adres = String.valueOf(data.getOrDefault("adres", ""));
                        if (!adres.isBlank() && !"null".equals(adres)) {
                            var resolved = regionCodeResolver.resolveFromAddress(adres);
                            if (resolved != null && resolved.adminLevel() == EAdminLevel.HJDONG) {
                                regionCode = resolved.code();
                                level = EAdminLevel.HJDONG;
                            } else {
                                // fallback: VWorld 지오코딩 (rate limit 방지: 50ms 간격)
                                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                                String hjdongCode = regionCodeResolver.geocodeAddress(adres);
                                if (hjdongCode != null) {
                                    regionCode = hjdongCode;
                                    level = EAdminLevel.HJDONG;
                                } else if (resolved != null) {
                                    regionCode = resolved.code();
                                    level = resolved.adminLevel();
                                } else {
                                    regionCode = String.valueOf(data.getOrDefault("region", ""));
                                }
                            }
                        } else {
                            regionCode = String.valueOf(data.getOrDefault("region", ""));
                        }

                        StatisticsRecord record = StatisticsRecord.create(
                                null, EStatisticsCategory.EDUCATION, regionCode,
                                level, null, "2025", data);
                        allRecords.add(record);
                    }

                    if (content.size() < 100) {
                        hasMore = false;
                    } else {
                        page++;
                    }

                    Thread.sleep(100);

                } catch (Exception e) {
                    log.error("[커리어넷] API 호출 실패 - gubun={}, error={}", gubun, e.getMessage(), e);
                    hasMore = false;
                }
            }
        }

        log.info("[커리어넷] 학교정보 수집 완료 - {}건", allRecords.size());
        return allRecords;
    }
}
