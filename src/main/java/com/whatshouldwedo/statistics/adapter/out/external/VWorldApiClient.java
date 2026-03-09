package com.whatshouldwedo.statistics.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.config.PublicDataApiProperties;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import com.whatshouldwedo.statistics.application.service.RegionCodeResolver;
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
public class VWorldApiClient {

    @Value("${public-data.api.vworld.base-url}")
    private String baseUrl;
    private static final String BBOX_KOREA = "BOX(124.5,33.0,132.0,38.7)";
    private static final String CRS = "EPSG:4326";

    private final PublicDataApiProperties apiProperties;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final RegionCodeResolver regionCodeResolver;

    public List<StatisticsRecord> collectElderlyWelfareFacilities() {
        log.info("[V-World] 노인복지시설 수집 시작");
        return collectVWorldData(
                "LT_P_MGPRTFB",
                EStatisticsCategory.WELFARE, "노인복지시설",
                EAdminLevel.HJDONG, false);
    }

    public List<StatisticsRecord> collectTouristAttractions() {
        log.info("[V-World] 관광지 수집 시작");
        return collectVWorldData(
                "LT_C_UO601",
                EStatisticsCategory.CULTURE, "관광지",
                EAdminLevel.SIGUNGU, true);
    }

    public List<StatisticsRecord> collectFireStations() {
        log.info("[V-World] 소방서 관할구역 수집 시작");
        return collectVWorldData(
                "LT_C_USFSFFB",
                EStatisticsCategory.SAFETY, "소방서 관할구역",
                EAdminLevel.SIGUNGU, true);
    }

    public List<StatisticsRecord> collectChildWelfareFacilities() {
        log.info("[V-World] 아동복지시설 수집 시작");
        return collectVWorldData(
                "LT_P_MGPRTFC",
                EStatisticsCategory.WELFARE, "아동복지시설",
                EAdminLevel.HJDONG, false);
    }

    public List<StatisticsRecord> collectDisasterRiskZones() {
        log.info("[V-World] 재해위험지구 수집 시작");
        return collectVWorldData(
                "LT_C_UP201",
                EStatisticsCategory.SAFETY, "재해위험지구",
                EAdminLevel.SIGUNGU, true);
    }

    public List<StatisticsRecord> collectMainCommercialAreas() {
        log.info("[V-World] 주요상권 수집 시작");
        return collectVWorldData(
                "LT_C_DGMAINBIZ",
                EStatisticsCategory.ECONOMY, "주요상권",
                EAdminLevel.SIGUNGU, true);
    }

    public List<StatisticsRecord> collectDogParks() {
        log.info("[V-World] 반려견놀이터 수집 시작");
        List<StatisticsRecord> records = collectVWorldData(
                "LT_C_DOGPARK",
                EStatisticsCategory.WELFARE, "반려견놀이터",
                EAdminLevel.SIGUNGU, true);
        return resolveByAddressField(records, "addr", "반려견놀이터");
    }

    public List<StatisticsRecord> collectUrbanLandUse() {
        log.info("[V-World] 도시지역 용도 수집 시작");
        return collectVWorldData(
                "LT_C_UQ111",
                EStatisticsCategory.HOUSING, "도시지역 용도",
                EAdminLevel.SIGUNGU, true);
    }

    public List<StatisticsRecord> collectTraditionalMarkets() {
        log.info("[V-World] 전통시장 수집 시작");
        return collectVWorldData(
                "LT_P_TRADSIJANG",
                EStatisticsCategory.ECONOMY, "전통시장",
                EAdminLevel.HJDONG, false);
    }

    public List<StatisticsRecord> collectNationalPlanZone() {
        log.info("[V-World] 국토계획구역 수집 시작");
        return collectVWorldData(
                "LT_C_UQ141",
                EStatisticsCategory.HOUSING, "국토계획구역",
                EAdminLevel.SIGUNGU, true);
    }

    public List<StatisticsRecord> collectUrbanPlanTransport() {
        log.info("[V-World] 도시계획 교통시설 수집 시작");
        return collectVWorldData(
                "LT_C_UPISUQ152",
                EStatisticsCategory.TRANSPORT, "도시계획 교통시설",
                EAdminLevel.SIGUNGU, true);
    }

    private List<StatisticsRecord> collectVWorldData(
            String dataName,
            EStatisticsCategory category, String categoryItemName,
            EAdminLevel adminLevel, boolean skipGeometry) {

        String apiKey = apiProperties.getVworld().getKey();
        if (apiKey.isBlank()) {
            log.warn("[V-World] API 키가 설정되지 않았습니다. 수집을 건너뜁니다.");
            return List.of();
        }

        List<StatisticsRecord> allRecords = new ArrayList<>();
        int page = 1;
        // 폴리곤 데이터는 1건당 ~100KB 이상이므로 page size를 줄임
        int size = skipGeometry ? 100 : 50;
        boolean hasMore = true;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(64 * 1024 * 1024))
                .build();
        WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

        while (hasMore) {
            try {
                int currentPage = page;

                String response = webClient.get()
                        .uri(baseUrl, uriBuilder -> uriBuilder
                                .queryParam("service", "data")
                                .queryParam("request", "GetFeature")
                                .queryParam("data", dataName)
                                .queryParam("key", apiKey)
                                .queryParam("format", "json")
                                .queryParam("size", String.valueOf(size))
                                .queryParam("page", String.valueOf(currentPage))
                                .queryParam("geomFilter", BBOX_KOREA)
                                .queryParam("crs", CRS)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response == null || response.isBlank()) break;

                JsonNode root = objectMapper.readTree(response);
                String status = root.path("response").path("status").asText("");
                if (!"OK".equals(status)) {
                    String errorText = root.path("response").path("error").path("text").asText("");
                    log.error("[V-World] API 에러 - data={}, error={}", dataName, errorText);
                    break;
                }

                JsonNode features = root.path("response").path("result")
                        .path("featureCollection").path("features");
                if (!features.isArray() || features.isEmpty()) {
                    hasMore = false;
                    continue;
                }

                for (JsonNode feature : features) {
                    JsonNode props = feature.path("properties");

                    Map<String, Object> data = new HashMap<>();
                    props.fields().forEachRemaining(e -> data.put(e.getKey(), e.getValue().asText("")));
                    data.put("source", "V-World");
                    data.put("category_item", categoryItemName);

                    String regionCode;
                    EAdminLevel resolvedLevel;

                    if (!skipGeometry) {
                        // Point 데이터: 좌표 추출 + 역지오코딩으로 HJDONG 확정
                        JsonNode geometry = feature.path("geometry");
                        if (geometry.has("coordinates") && "Point".equals(geometry.path("type").asText(""))) {
                            JsonNode coords = geometry.path("coordinates");
                            double lon = coords.get(0).asDouble();
                            double lat = coords.get(1).asDouble();
                            data.put("longitude", lon);
                            data.put("latitude", lat);

                            // 역지오코딩으로 행정동코드 확정
                            String hjdongCode = regionCodeResolver.reverseGeocode(lon, lat);
                            if (hjdongCode != null) {
                                regionCode = hjdongCode;
                                resolvedLevel = EAdminLevel.HJDONG;
                            } else {
                                // 역지오코딩 실패 시 속성 기반 추출 (fallback)
                                regionCode = extractRegionCode(props);
                                resolvedLevel = resolveAdminLevel(regionCode, adminLevel);
                            }
                        } else {
                            regionCode = extractRegionCode(props);
                            resolvedLevel = resolveAdminLevel(regionCode, adminLevel);
                        }
                    } else {
                        // 폴리곤 데이터: 속성 기반 추출 (시군구 단위)
                        regionCode = extractRegionCode(props);
                        resolvedLevel = resolveAdminLevel(regionCode, adminLevel);
                    }

                    StatisticsRecord record = StatisticsRecord.create(
                            null, category, regionCode, resolvedLevel, null, "2025", data);
                    allRecords.add(record);
                }

                int total = root.path("response").path("record").path("total").asInt(0);
                int current = root.path("response").path("record").path("current").asInt(0);
                if (current >= total || features.size() < size) {
                    hasMore = false;
                } else {
                    page++;
                }

                Thread.sleep(100);

            } catch (Exception e) {
                log.error("[V-World] API 호출 실패 - data={}, page={}, error={}",
                        dataName, page, e.getMessage(), e);
                hasMore = false;
            }
        }

        log.info("[V-World] {} 수집 완료 - {}건", categoryItemName, allRecords.size());
        return allRecords;
    }

    private String extractRegionCode(JsonNode props) {
        // 0) 행정동코드 직접 필드 (emd_cd, hjd_cd, adm_cd 등)
        for (String field : new String[]{"hjd_cd", "emd_cd", "adm_cd", "adm_dr_cd"}) {
            String code = props.path(field).asText("");
            if (!code.isBlank() && code.length() >= 7) {
                return code;
            }
        }

        // 1) signgu_se / ward_id (시군구코드 5자리) - 숫자코드를 텍스트보다 우선
        String signguSe = props.path("signgu_se").asText("");
        if (!signguSe.isBlank() && signguSe.length() >= 5 && signguSe.matches("\\d+")) {
            return signguSe.substring(0, 5);
        }
        String wardId = props.path("ward_id").asText("");
        if (!wardId.isBlank() && wardId.length() >= 5 && wardId.matches("\\d+")) {
            return wardId.substring(0, 5);
        }

        // 2) sido_name + sigg_name + emd_name (읍면동까지 추출 → postProcess에서 정규화됨)
        String sido = props.path("sido_name").asText("");
        String sigg = props.path("sigg_name").asText("");
        String emd = props.path("emd_name").asText("");
        if (sido.isBlank()) sido = props.path("sido").asText("");
        if (sido.isBlank()) sido = props.path("sd_nm").asText("");
        if (sigg.isBlank()) sigg = props.path("sigg").asText("");
        if (sigg.isBlank()) sigg = props.path("sgg_nm").asText("");
        if (emd.isBlank()) emd = props.path("emd").asText("");
        if (emd.isBlank()) emd = props.path("emd_nm").asText("");
        if (!sido.isBlank() && !sigg.isBlank()) {
            if (!emd.isBlank()) {
                return sido + " " + sigg + " " + emd;
            }
            return sido + " " + sigg;
        }
        if (!sido.isBlank()) return sido;

        // 3) 주소 파싱 - 읍면동까지 추출 (3번째 토큰)
        String address = findFirstNonBlank(props,
                "fac_o_add", "fac_n_add", "adr_jibun", "adr_road");
        if (!address.isBlank()) {
            String[] parts = address.split("\\s+");
            if (parts.length >= 3) return parts[0] + " " + parts[1] + " " + parts[2];
            if (parts.length >= 2) return parts[0] + " " + parts[1];
            if (parts.length > 0) return parts[0];
        }

        return "";
    }

    private EAdminLevel resolveAdminLevel(String regionCode, EAdminLevel defaultLevel) {
        if (regionCode == null || regionCode.isBlank()) return defaultLevel;
        // 숫자코드: 길이로 판단
        if (regionCode.matches("\\d+")) {
            int len = regionCode.length();
            if (len >= 7) return EAdminLevel.HJDONG;
            if (len == 5) return EAdminLevel.SIGUNGU;
            if (len == 2) return EAdminLevel.SIDO;
        }
        // 텍스트: 공백 3개 이상이면 읍면동 포함
        String[] parts = regionCode.split("\\s+");
        if (parts.length >= 3) return EAdminLevel.HJDONG;
        if (parts.length == 2) return EAdminLevel.SIGUNGU;
        if (parts.length == 1) return EAdminLevel.SIDO;
        return defaultLevel;
    }

    /**
     * 주소 필드를 이용하여 SIGUNGU 레코드를 HJDONG으로 업그레이드
     */
    private List<StatisticsRecord> resolveByAddressField(List<StatisticsRecord> records, String addrField, String label) {
        List<StatisticsRecord> result = new ArrayList<>(records.size());
        int resolved = 0;
        for (StatisticsRecord r : records) {
            if (r.getAdminLevel() == EAdminLevel.HJDONG) {
                result.add(r);
                continue;
            }
            Map<String, Object> data = r.getData();
            String addr = String.valueOf(data.getOrDefault(addrField, ""));
            if (!addr.isBlank() && !"null".equals(addr)) {
                // 1) resolveFromAddress (주소 파싱)
                var res = regionCodeResolver.resolveFromAddress(addr);
                if (res != null && res.adminLevel() == EAdminLevel.HJDONG) {
                    result.add(StatisticsRecord.create(
                            r.getDatasetId(), r.getCategoryCode(), res.code(),
                            EAdminLevel.HJDONG, r.getHjdongVersionId(), r.getDataYear(), data));
                    resolved++;
                    continue;
                }
                // 2) VWorld 지오코딩 fallback
                String hjdongCode = regionCodeResolver.geocodeAddress(addr);
                if (hjdongCode != null) {
                    result.add(StatisticsRecord.create(
                            r.getDatasetId(), r.getCategoryCode(), hjdongCode,
                            EAdminLevel.HJDONG, r.getHjdongVersionId(), r.getDataYear(), data));
                    resolved++;
                    continue;
                }
            }
            result.add(r);
        }
        log.info("[V-World] {} HJDONG 변환 결과 - {}/{}건 성공", label, resolved, records.size());
        return result;
    }

    private String findFirstNonBlank(JsonNode props, String... fields) {
        for (String field : fields) {
            String val = props.path(field).asText("");
            if (!val.isBlank()) return val;
        }
        return "";
    }
}
