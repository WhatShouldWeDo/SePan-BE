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
public class SeoulDataApiClient {

    @Value("${public-data.api.seoul.base-url}")
    private String baseUrl;

    private final PublicDataApiProperties apiProperties;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final RegionCodeResolver regionCodeResolver;

    public List<StatisticsRecord> collectConstructionWork() {
        log.info("[서울데이터] 건설알림이 사업개요 수집 시작");

        String apiKey = apiProperties.getSeoulData().getKey();
        if (apiKey.isBlank()) {
            log.warn("[서울데이터] API 키가 설정되지 않았습니다.");
            return List.of();
        }

        List<StatisticsRecord> records = new ArrayList<>();
        int start = 1;
        int size = 1000;
        boolean hasMore = true;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        while (hasMore) {
            try {
                int end = start + size - 1;
                String url = String.format("%s/%s/json/ListConstructionWorkService/%d/%d/",
                        baseUrl, apiKey, start, end);

                String response = webClient.get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) break;

                JsonNode root = objectMapper.readTree(response);
                JsonNode service = root.path("ListConstructionWorkService");

                String resultCode = service.path("RESULT").path("CODE").asText("");
                if (!"INFO-000".equals(resultCode)) {
                    log.warn("[서울데이터] API 에러 - code={}, msg={}",
                            resultCode, service.path("RESULT").path("MESSAGE").asText(""));
                    break;
                }

                int totalCount = service.path("list_total_count").asInt(0);
                JsonNode rows = service.path("row");

                if (!rows.isArray() || rows.isEmpty()) break;

                for (JsonNode row : rows) {
                    Map<String, Object> data = objectMapper.convertValue(row, new TypeReference<>() {});

                    data.put("source", "서울데이터");
                    data.put("category_item", "건설사업 현황");

                    // 1) 좌표 역지오코딩으로 행정동 추출 (가장 정확)
                    String regionCode = null;
                    EAdminLevel level = EAdminLevel.SIGUNGU;

                    Object latObj = data.get("LAT");
                    Object lngObj = data.get("LNG");
                    if (latObj != null && lngObj != null) {
                        try {
                            double lat = Double.parseDouble(String.valueOf(latObj));
                            double lng = Double.parseDouble(String.valueOf(lngObj));
                            if (lat != 0 && lng != 0) {
                                String hjdongCode = regionCodeResolver.reverseGeocode(lng, lat);
                                if (hjdongCode != null) {
                                    regionCode = hjdongCode;
                                    level = EAdminLevel.HJDONG;
                                }
                            }
                        } catch (NumberFormatException ignored) {}
                    }

                    // 2) 주소 필드에서 행정동 추출 시도
                    if (regionCode == null) {
                        for (String addrField : new String[]{"SITE_ADDR", "OFC_ADDR"}) {
                            String addr = String.valueOf(data.getOrDefault(addrField, ""));
                            if (!addr.isBlank() && !"null".equals(addr)) {
                                var resolved = regionCodeResolver.resolveFromAddress(addr);
                                if (resolved != null && resolved.adminLevel() == EAdminLevel.HJDONG) {
                                    regionCode = resolved.code();
                                    level = EAdminLevel.HJDONG;
                                    break;
                                }
                            }
                        }
                    }

                    // 3) VWorld 주소 지오코딩 fallback
                    if (regionCode == null) {
                        for (String addrField : new String[]{"SITE_ADDR", "OFC_ADDR"}) {
                            String addr = String.valueOf(data.getOrDefault(addrField, ""));
                            if (!addr.isBlank() && !"null".equals(addr)) {
                                String hjdongCode = regionCodeResolver.geocodeAddress(addr);
                                if (hjdongCode != null) {
                                    regionCode = hjdongCode;
                                    level = EAdminLevel.HJDONG;
                                    break;
                                }
                            }
                        }
                    }

                    // 4) 주소에서 동이름 추출 → "서울특별시 구 동" 형태로 HJDONG 매핑 시도
                    if (regionCode == null) {
                        String sggNm = String.valueOf(data.getOrDefault("SGG_NM", ""));
                        if (!sggNm.isBlank()) {
                            // SITE_ADDR에서 "동" 이름 추출 시도
                            for (String addrField : new String[]{"SITE_ADDR", "OFC_ADDR"}) {
                                String addr = String.valueOf(data.getOrDefault(addrField, ""));
                                if (!addr.isBlank() && !"null".equals(addr)) {
                                    String dongName = regionCodeResolver.extractDongFromDetail(addr);
                                    if (dongName != null) {
                                        String fullAddr = "서울특별시 " + sggNm + " " + dongName;
                                        var resolved = regionCodeResolver.resolveFromAddress(fullAddr);
                                        if (resolved != null && resolved.adminLevel() == EAdminLevel.HJDONG) {
                                            regionCode = resolved.code();
                                            level = EAdminLevel.HJDONG;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 5) fallback: SGG_NM(자치구명) → "서울특별시 자치구명" 으로 시군구 매핑
                    if (regionCode == null) {
                        String sggNm = String.valueOf(data.getOrDefault("SGG_NM", ""));
                        if (!sggNm.isBlank()) {
                            var normalized = regionCodeResolver.normalize(
                                    "서울특별시 " + sggNm, EAdminLevel.SIGUNGU);
                            regionCode = normalized.code();
                            level = normalized.adminLevel();
                        } else {
                            regionCode = "";
                        }
                    }

                    StatisticsRecord record = StatisticsRecord.create(
                            null, EStatisticsCategory.SAFETY, regionCode,
                            level, null, "2025", data);
                    records.add(record);
                }

                if (end >= totalCount) {
                    hasMore = false;
                } else {
                    start = end + 1;
                    Thread.sleep(100);
                }

            } catch (Exception e) {
                log.error("[서울데이터] API 호출 실패 - error={}", e.getMessage(), e);
                hasMore = false;
            }
        }

        log.info("[서울데이터] 건설사업 현황 수집 완료 - {}건", records.size());
        return records;
    }
}
