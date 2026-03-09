package com.whatshouldwedo.statistics.application.service;

import com.whatshouldwedo.region.application.port.out.SidoRepository;
import com.whatshouldwedo.region.application.port.out.SigunguRepository;
import com.whatshouldwedo.region.domain.Sido;
import com.whatshouldwedo.region.domain.Sigungu;
import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatshouldwedo.statistics.config.PublicDataApiProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionCodeResolver {

    @Value("${public-data.api.vworld.address-url}")
    private String vworldAddressUrl;

    private final SidoRepository sidoRepository;
    private final SigunguRepository sigunguRepository;
    private final PublicDataApiProperties apiProperties;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    private final Map<String, String> sidoNameToCode = new HashMap<>();
    private final Map<String, String> sigunguNameToCode = new HashMap<>();
    private final Map<String, String> sigunguFullNameToCode = new HashMap<>();
    private final Map<String, String> bjdongToHjdongMap = new HashMap<>();
    private final Map<String, String> kosisToHjdongMap = new HashMap<>();
    // 통계청 시군구코드 → 행정표준 시군구코드 매핑 (e.g., "31011" → "41010")
    private final Map<String, String> kosisToAdminSigungu = new HashMap<>();
    // key: "adminSigunguCode:dongName" (e.g., "41010:파장동"), value: hjdongCode (통계청 8자리)
    private final Map<String, String> dongNameToCode = new HashMap<>();
    // 법정동 시군구코드(5자리) → 행정동 시군구코드(5자리) 매핑 (e.g., "11110" → "11010")
    private final Map<String, String> bjdongSigunguToAdminSigungu = new HashMap<>();
    // 법정동 시군구코드+동명 → 법정동코드(10자리) (e.g., "11110:충신동" → "1111016700")
    private final Map<String, String> bjdongNameToCode = new HashMap<>();

    // 동/읍/면 추출 패턴 (프리컴파일)
    private static final Pattern DONG_EUP_MYEON_PATTERN = Pattern.compile("([가-힣]{1,10}[동읍면])(?:\\s|,|\\d|$)");

    private static final Map<String, String> SIDO_ALIAS = Map.ofEntries(
            Map.entry("서울", "서울특별시"),
            Map.entry("서울시", "서울특별시"),
            Map.entry("부산", "부산광역시"),
            Map.entry("부산시", "부산광역시"),
            Map.entry("대구", "대구광역시"),
            Map.entry("대구시", "대구광역시"),
            Map.entry("인천", "인천광역시"),
            Map.entry("인천시", "인천광역시"),
            Map.entry("광주", "광주광역시"),
            Map.entry("광주시", "광주광역시"),
            Map.entry("대전", "대전광역시"),
            Map.entry("대전시", "대전광역시"),
            Map.entry("울산", "울산광역시"),
            Map.entry("울산시", "울산광역시"),
            Map.entry("세종", "세종특별자치시"),
            Map.entry("세종시", "세종특별자치시"),
            Map.entry("경기", "경기도"),
            Map.entry("강원", "강원특별자치도"),
            Map.entry("강원도", "강원특별자치도"),  // 구 명칭
            Map.entry("충북", "충청북도"),
            Map.entry("충남", "충청남도"),
            Map.entry("전북", "전북특별자치도"),
            Map.entry("전라특별자치도", "전북특별자치도"),  // 2024 명칭변경
            Map.entry("전남", "전라남도"),
            Map.entry("경북", "경상북도"),
            Map.entry("경남", "경상남도"),
            Map.entry("제주", "제주특별자치도")
    );

    @PostConstruct
    public void init() {
        List<Sido> allSido = buildSidoCache();
        buildSigunguCache(allSido);
        loadBjdongHjdongMapping();
        loadKosisCodeMapping();
        loadKosisAdminSigunguMapping();
        loadBjdongNameMapping();
        buildDongNameCache();
        log.info("RegionCodeResolver 초기화 완료 - 시도 {}개, 시군구 {}개, 법정동매핑 {}개, KOSIS매핑 {}개, 시군구코드매핑 {}개, 동이름매핑 {}개, 법정동시군구매핑 {}개, 법정동명매핑 {}개",
                sidoNameToCode.size(), sigunguFullNameToCode.size(), bjdongToHjdongMap.size(),
                kosisToHjdongMap.size(), kosisToAdminSigungu.size(), dongNameToCode.size(), bjdongSigunguToAdminSigungu.size(), bjdongNameToCode.size());
    }

    private List<Sido> buildSidoCache() {
        List<Sido> allSido = sidoRepository.findAll();
        for (Sido sido : allSido) {
            sidoNameToCode.put(sido.getName(), sido.getCode());
        }
        // 약칭도 등록
        for (Map.Entry<String, String> alias : SIDO_ALIAS.entrySet()) {
            String code = sidoNameToCode.get(alias.getValue());
            if (code != null) {
                sidoNameToCode.put(alias.getKey(), code);
            }
        }
        return allSido;
    }

    private void buildSigunguCache(List<Sido> allSido) {
        Map<String, String> sidoCodeToName = new HashMap<>();
        for (Sido sido : allSido) {
            sidoCodeToName.put(sido.getCode(), sido.getName());
        }

        for (Sigungu sigungu : sigunguRepository.findAll()) {
            // 시군구명 단독으로는 중복 가능 (서울 중구, 부산 중구), 코드로만 매핑
            sigunguNameToCode.putIfAbsent(sigungu.getName(), sigungu.getCode());

            // "시도 시군구" 풀네임으로 매핑
            String sidoCode = sigungu.getCode().substring(0, 2);
            String sidoName = sidoCodeToName.get(sidoCode);
            if (sidoName != null) {
                sigunguFullNameToCode.put(sidoName + " " + sigungu.getName(), sigungu.getCode());
                // 약칭 조합도 등록
                for (Map.Entry<String, String> alias : SIDO_ALIAS.entrySet()) {
                    if (alias.getValue().equals(sidoName)) {
                        sigunguFullNameToCode.put(alias.getKey() + " " + sigungu.getName(), sigungu.getCode());
                    }
                }
            }
        }
    }

    private void loadBjdongHjdongMapping() {
        ClassPathResource resource = new ClassPathResource("data/bjdong_hjdong_mapping.csv");
        if (!resource.exists()) {
            log.info("법정동-행정동 매핑 파일 없음 (스킵)");
            return;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    String bjdong = parts[0].trim();
                    String hjdong = parts[1].trim();
                    bjdongToHjdongMap.putIfAbsent(bjdong, hjdong);
                    // 법정동 시군구(5자리) → 행정동 시군구(5자리) 매핑 구축
                    if (bjdong.length() >= 5 && hjdong.length() >= 5) {
                        bjdongSigunguToAdminSigungu.putIfAbsent(bjdong.substring(0, 5), hjdong.substring(0, 5));
                    }
                }
            }
        } catch (Exception e) {
            log.error("법정동-행정동 매핑 로딩 실패: {}", e.getMessage());
        }
    }

    private void loadKosisCodeMapping() {
        ClassPathResource resource = new ClassPathResource("data/hjdong/kosis_code_mapping.csv");
        if (!resource.exists()) {
            log.info("KOSIS 코드 매핑 파일 없음 (스킵)");
            return;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    kosisToHjdongMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (Exception e) {
            log.error("KOSIS 코드 매핑 로딩 실패: {}", e.getMessage());
        }
    }

    private void loadKosisAdminSigunguMapping() {
        ClassPathResource resource = new ClassPathResource("data/hjdong/kosis_admin_sigungu_mapping.csv");
        if (!resource.exists()) {
            log.info("통계청-행정표준 시군구 매핑 파일 없음 (스킵)");
            return;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    kosisToAdminSigungu.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (Exception e) {
            log.error("통계청-행정표준 시군구 매핑 로딩 실패: {}", e.getMessage());
        }
    }

    private void loadBjdongNameMapping() {
        ClassPathResource resource = new ClassPathResource("data/bjdong_code_name.csv");
        if (!resource.exists()) {
            log.info("법정동코드-이름 매핑 파일 없음 (스킵)");
            return;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String bjdongCode = parts[0].trim();
                    String bjdongSigungu = parts[1].trim();
                    String dongName = parts[2].trim();
                    bjdongNameToCode.putIfAbsent(bjdongSigungu + ":" + dongName, bjdongCode);
                }
            }
        } catch (Exception e) {
            log.error("법정동코드-이름 매핑 로딩 실패: {}", e.getMessage());
        }
    }

    private void buildDongNameCache() {
        // 최신 행정동 CSV(2026)에서 "adminSigunguCode:dongName" → hjdongCode 매핑 구축
        // CSV의 sigunguCode는 통계청 코드이므로 행정표준코드로 변환하여 키 등록
        ClassPathResource resource = new ClassPathResource("data/hjdong/hjdong_2026.csv");
        if (!resource.exists()) {
            log.info("행정동 CSV 없음 - 동이름 매핑 스킵");
            return;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String code = parts[0].trim();
                    String name = parts[1].trim();
                    String kosisSigungu = parts[2].trim();
                    // 행정표준 시군구코드로 변환하여 등록
                    String adminSigungu = kosisToAdminSigungu.getOrDefault(kosisSigungu, kosisSigungu);
                    dongNameToCode.put(adminSigungu + ":" + name, code);
                    // 통계청 시군구코드로도 등록 (법정동→행정동 매핑 시 필요)
                    if (!kosisSigungu.equals(adminSigungu)) {
                        dongNameToCode.putIfAbsent(kosisSigungu + ":" + name, code);
                    }
                }
            }
        } catch (Exception e) {
            log.error("행정동 동이름 캐시 구축 실패: {}", e.getMessage());
        }
    }

    /**
     * 원시 regionCode를 정규화된 코드로 변환
     */
    public NormalizedRegion normalize(String rawCode, EAdminLevel originalLevel) {
        if (rawCode == null || rawCode.isBlank()) {
            return new NormalizedRegion(rawCode, originalLevel);
        }

        String trimmed = rawCode.trim();

        // 1. 순수 숫자 코드
        if (trimmed.matches("\\d+")) {
            return normalizeNumericCode(trimmed, originalLevel);
        }

        // 2. 텍스트 주소 (시도 시군구 형태)
        return normalizeTextAddress(trimmed, originalLevel);
    }

    private NormalizedRegion normalizeNumericCode(String code, EAdminLevel originalLevel) {
        int len = code.length();

        // 2자리: 시도코드
        if (len == 2) {
            return new NormalizedRegion(code, EAdminLevel.SIDO);
        }

        // 5자리: 시군구코드
        if (len == 5) {
            return new NormalizedRegion(code, EAdminLevel.SIGUNGU);
        }

        // 7자리: KOSIS 행정동코드 → 8자리 정규화 (kosis_code_mapping 우선)
        if (len == 7) {
            String hjdong8 = kosisToHjdongMap.get(code);
            if (hjdong8 != null) {
                return new NormalizedRegion(hjdong8, EAdminLevel.HJDONG);
            }
            // 매핑 없으면 뒤에 0 추가하여 8자리로
            return new NormalizedRegion(code + "0", EAdminLevel.HJDONG);
        }

        // 8자리: 행정동코드 (2022년 이후 체계)
        if (len == 8) {
            return new NormalizedRegion(code, EAdminLevel.HJDONG);
        }

        // 10자리: 법정동코드 또는 행정동코드
        if (len == 10) {
            // 법정동→행정동 매핑 시도
            String hjdongCode = bjdongToHjdongMap.get(code);
            if (hjdongCode != null) {
                return new NormalizedRegion(hjdongCode, EAdminLevel.HJDONG);
            }
            // 매핑 없으면 행정동코드로 간주
            return new NormalizedRegion(code, originalLevel != null ? originalLevel : EAdminLevel.HJDONG);
        }

        // 기타: 원본 유지
        return new NormalizedRegion(code, originalLevel);
    }

    private NormalizedRegion normalizeTextAddress(String address, EAdminLevel originalLevel) {
        // "시도명 시군구명" 패턴 직접 매칭
        String code = sigunguFullNameToCode.get(address);
        if (code != null) {
            return new NormalizedRegion(code, EAdminLevel.SIGUNGU);
        }

        // 시도명 단독 매칭 (1단어)
        String sidoCode = sidoNameToCode.get(address);
        if (sidoCode != null) {
            return new NormalizedRegion(sidoCode, EAdminLevel.SIDO);
        }

        // 공백으로 분리하여 시도+시군구(+읍면동) 매칭
        String[] parts = address.split("\\s+");
        if (parts.length >= 2) {
            String sidoPart = parts[0];
            String sigunguPart = parts[1];

            // 시도명 정규화
            String sidoFullName = SIDO_ALIAS.getOrDefault(sidoPart, sidoPart);
            String fullName = sidoFullName + " " + sigunguPart;
            code = sigunguFullNameToCode.get(fullName);
            int dongIndex = 2; // 읍면동이 위치하는 인덱스

            // 3단계 시군구 (수원시 장안구, 성남시 분당구 등) 처리
            if (code == null && parts.length >= 3) {
                fullName = sidoFullName + " " + sigunguPart + " " + parts[2];
                code = sigunguFullNameToCode.get(fullName);
                dongIndex = 3;
            }

            // "시도 시군구" 합체 시도 (예: "수원시장안구")
            if (code == null && parts.length >= 3) {
                fullName = sidoFullName + " " + sigunguPart + parts[2];
                code = sigunguFullNameToCode.get(fullName);
                dongIndex = 3;
            }

            if (code != null) {
                // 읍면동까지 있으면 행정동코드로 매핑 시도
                if (parts.length > dongIndex) {
                    String dongName = parts[dongIndex];
                    String hjdongCode = findHjdongByDongName(code, dongName);
                    if (hjdongCode != null) {
                        return new NormalizedRegion(hjdongCode, EAdminLevel.HJDONG);
                    }
                }
                return new NormalizedRegion(code, EAdminLevel.SIGUNGU);
            }

            // 시군구 매칭 실패 시 - "세종특별자치시 세종특별자치시" 중복 처리
            if (sidoPart.equals(sigunguPart)) {
                sidoCode = sidoNameToCode.get(sidoFullName);
                if (sidoCode != null) {
                    return new NormalizedRegion(sidoCode, EAdminLevel.SIDO);
                }
            }

            // 시군구 매칭 실패 시 - "서울특별시 서울지역외" 등 특수 패턴 → 시도로 fallback
            sidoCode = sidoNameToCode.get(sidoFullName);
            if (sidoCode != null) {
                // 시군구명만으로 매칭 재시도
                String sggCode = sigunguNameToCode.get(sigunguPart);
                if (sggCode != null) {
                    return new NormalizedRegion(sggCode, EAdminLevel.SIGUNGU);
                }
                // 시도 코드라도 반환
                return new NormalizedRegion(sidoCode, EAdminLevel.SIDO);
            }
        }

        log.debug("regionCode 정규화 실패 - rawCode={}", address);
        return new NormalizedRegion(address, originalLevel);
    }

    // 괄호 안 동명 추출 패턴: "(송파동)", "(신대방동, XX아파트)", "(동명/학교명)"
    private static final Pattern PAREN_DONG_PATTERN = Pattern.compile("\\(([가-힣]+[동읍면리])(?:[,/].*)?\\)");

    /**
     * 도로명주소에서 괄호 안 동명을 추출하여 행정동 매핑 시도
     * 예: "서울특별시 송파구 송이로 42" + "(송파동/가락고등학교)" → 송파동
     * 예: "서울특별시 동작구 신대방16다길 14 (신대방동)" → 신대방동
     */
    public NormalizedRegion resolveFromAddress(String address) {
        if (address == null || address.isBlank()) return null;

        // 주소 전처리: "전북특별자치도교육청전주시" → "전북특별자치도 전주시"
        String cleaned = address.replaceAll("교육청", " ").replaceAll("\\s+", " ").trim();

        String[] mainParts = cleaned.split("\\s+");
        if (mainParts.length < 2) return null;

        // 시도 + 시군구 추출
        String sidoFullName = SIDO_ALIAS.getOrDefault(mainParts[0], mainParts[0]);
        String fullName = sidoFullName + " " + mainParts[1];
        String sigunguCode = sigunguFullNameToCode.get(fullName);
        int dongStartIndex = 2; // 동/읍/면이 시작되는 인덱스

        // 3단계 시군구 (수원시 장안구, 성남시 분당구 등) 처리
        if (sigunguCode == null && mainParts.length >= 3) {
            fullName = sidoFullName + " " + mainParts[1] + " " + mainParts[2];
            sigunguCode = sigunguFullNameToCode.get(fullName);
            if (sigunguCode != null) {
                dongStartIndex = 3;
            }
        }

        // 세종특별자치시 특수 처리: 시군구 없이 바로 도로명
        if (sigunguCode == null && "세종특별자치시".equals(sidoFullName)) {
            sigunguCode = sigunguFullNameToCode.get("세종특별자치시 세종시");
            if (sigunguCode == null) {
                // "세종" 시도코드로 유일한 시군구 사용
                String sidoCode = sidoNameToCode.get("세종특별자치시");
                if (sidoCode != null) {
                    for (Map.Entry<String, String> entry : sigunguFullNameToCode.entrySet()) {
                        if (entry.getValue().startsWith(sidoCode)) {
                            sigunguCode = entry.getValue();
                            break;
                        }
                    }
                }
            }
        }

        if (sigunguCode == null) return null;

        // 1) 괄호 안 동명 추출 시도
        Matcher m = PAREN_DONG_PATTERN.matcher(address);
        if (m.find()) {
            String dongName = m.group(1);
            String hjdongCode = findHjdongByDongName(sigunguCode, dongName);
            if (hjdongCode != null) {
                return new NormalizedRegion(hjdongCode, EAdminLevel.HJDONG);
            }
            log.debug("resolveFromAddress: 괄호 동명 발견 but 매핑 실패 - sigungu={}, dong={}", sigunguCode, dongName);
        }

        // 2) 시군구 다음 토큰이 동/읍/면/리로 끝나면 행정동 매핑 시도
        if (mainParts.length > dongStartIndex) {
            String dongToken = mainParts[dongStartIndex];
            if (dongToken.matches(".*[동읍면리]$") && !dongToken.matches(".*[로길]$")) {
                String hjdongCode = findHjdongByDongName(sigunguCode, dongToken);
                if (hjdongCode != null) {
                    return new NormalizedRegion(hjdongCode, EAdminLevel.HJDONG);
                }
            }
        }

        return new NormalizedRegion(sigunguCode, EAdminLevel.SIGUNGU);
    }

    /**
     * 주소 텍스트에서 동/읍/면 이름을 추출.
     * 괄호 안 패턴 우선, 없으면 일반 텍스트에서 추출.
     * 예: "(송파동/가락고등학교)" → "송파동"
     * 예: "서울시 강동구 고덕동, 강일동 일원" → "고덕동"
     * 예: "서울특별시 강서구 마곡동 마곡도시개발사업구역" → "마곡동"
     */
    public String extractDongFromDetail(String detail) {
        if (detail == null || detail.isBlank()) return null;
        // 1) 괄호 안 패턴
        Matcher m = PAREN_DONG_PATTERN.matcher(detail);
        if (m.find()) return m.group(1);
        // 2) 일반 텍스트에서 동/읍/면 추출 (첫 번째 매칭)
        Matcher m2 = DONG_EUP_MYEON_PATTERN.matcher(detail);
        if (m2.find()) {
            String candidate = m2.group(1);
            // "강동" 같은 구 이름 필터링 (2글자 이하 + "동"만 있는 경우 제외)
            if (candidate.length() <= 2) return null;
            return candidate;
        }
        return null;
    }

    /**
     * 시군구코드 + 동이름 → 행정동코드 매핑
     */
    public String resolveDongCode(String sigunguCode, String dongName) {
        if (sigunguCode == null || dongName == null) return null;
        return dongNameToCode.get(sigunguCode + ":" + dongName);
    }

    /**
     * 시군구코드 + 동이름으로 행정동코드 찾기 (exact match → prefix match)
     * 법정동명과 행정동명이 다를 수 있으므로 유연한 매칭 시도.
     * 예: 법정동 "도곡동" → 행정동 "도곡1동" or "도곡2동"
     */
    public String findHjdongByDongName(String sigunguCode, String dongName) {
        if (sigunguCode == null || dongName == null || dongName.isBlank()) return null;

        // 시군구코드 후보: 원본, 법정동→행정동 변환, 행정표준 변환
        String bjdongAdmin = bjdongSigunguToAdminSigungu.getOrDefault(sigunguCode, sigunguCode);
        String kosisAdmin = kosisToAdminSigungu.getOrDefault(sigunguCode, sigunguCode);
        String kosisAdmin2 = kosisToAdminSigungu.getOrDefault(bjdongAdmin, bjdongAdmin);

        // 중복 제거하여 후보 목록 구성
        List<String> candidates = new ArrayList<>();
        candidates.add(sigunguCode);
        if (!bjdongAdmin.equals(sigunguCode)) candidates.add(bjdongAdmin);
        if (!kosisAdmin.equals(sigunguCode) && !kosisAdmin.equals(bjdongAdmin)) candidates.add(kosisAdmin);
        if (!kosisAdmin2.equals(bjdongAdmin) && !candidates.contains(kosisAdmin2)) candidates.add(kosisAdmin2);

        String baseName = dongName.replaceAll("[0-9]*[동읍면리가]$", "");

        for (String sgg : candidates) {
            // 1) exact match
            String exact = dongNameToCode.get(sgg + ":" + dongName);
            if (exact != null) return exact;

            // 2) prefix match: "도곡동" → "도곡1동", "도곡2동" 중 첫 번째
            if (!baseName.isBlank()) {
                String prefix = sgg + ":" + baseName;
                for (Map.Entry<String, String> entry : dongNameToCode.entrySet()) {
                    if (entry.getKey().startsWith(prefix)) {
                        log.debug("findHjdongByDongName prefix match: {} → {} → {}", dongName, entry.getKey(), entry.getValue());
                        return entry.getValue();
                    }
                }
            }
        }

        // 3) 법정동명 → 법정동코드 → bjdongToHjdongMap → 행정동코드
        String bjdongCode = bjdongNameToCode.get(sigunguCode + ":" + dongName);
        if (bjdongCode != null) {
            String hjdongCode = bjdongToHjdongMap.get(bjdongCode);
            if (hjdongCode != null) {
                log.debug("findHjdongByDongName bjdong path: {}:{} → {} → {}", sigunguCode, dongName, bjdongCode, hjdongCode);
                return hjdongCode;
            }
        }

        log.debug("findHjdongByDongName MISS: sigungu={}, dong={}", sigunguCode, dongName);
        return null;
    }

    // 역지오코딩 결과 캐시: "lon,lat" → hjdongCode (소수점 3자리로 반올림하여 근접 좌표 재활용)
    private final Map<String, String> geocodeCache = new ConcurrentHashMap<>();

    /**
     * 좌표(경도, 위도)로 VWorld 역지오코딩하여 행정동코드 반환
     * @return 행정동코드(10자리) 또는 null
     */
    public String reverseGeocode(double longitude, double latitude) {
        // 소수점 3자리로 반올림하여 캐시 키 생성 (~100m 정밀도)
        String cacheKey = String.format("%.3f,%.3f", longitude, latitude);
        String cached = geocodeCache.get(cacheKey);
        if (cached != null) return cached.isEmpty() ? null : cached;
        try {
            String apiKey = apiProperties.getVworld().getKey();
            if (apiKey == null || apiKey.isBlank()) return null;

            ExchangeStrategies strategies = ExchangeStrategies.builder()
                    .codecs(c -> c.defaultCodecs().maxInMemorySize(1024 * 1024))
                    .build();
            WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

            String response = webClient.get()
                    .uri(vworldAddressUrl, uriBuilder -> uriBuilder
                            .queryParam("service", "address")
                            .queryParam("request", "getAddress")
                            .queryParam("version", "2.0")
                            .queryParam("crs", "epsg:4326")
                            .queryParam("point", longitude + "," + latitude)
                            .queryParam("format", "json")
                            .queryParam("type", "both")
                            .queryParam("simple", "false")
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                geocodeCache.put(cacheKey, "");
                return null;
            }

            JsonNode root = objectMapper.readTree(response);
            String status = root.path("response").path("status").asText("");
            if (!"OK".equals(status)) {
                log.info("역지오코딩 NOT OK - lon={}, lat={}, status={}", longitude, latitude, status);
                geocodeCache.put(cacheKey, "");
                return null;
            }

            JsonNode result = root.path("response").path("result");
            if (result.isArray() && !result.isEmpty()) {
                // 1차: level4AC(행정동코드) 직접 추출
                for (JsonNode item : result) {
                    String adminCode = item.path("structure").path("level4AC").asText("");
                    if (!adminCode.isBlank() && adminCode.length() >= 8) {
                        geocodeCache.put(cacheKey, adminCode);
                        return adminCode;
                    }
                }
                // 2차: level4LC(법정동코드) → bjdongToHjdongMap → 행정동코드 변환
                for (JsonNode item : result) {
                    String bjdongCode = item.path("structure").path("level4LC").asText("");
                    if (!bjdongCode.isBlank() && bjdongCode.length() >= 10) {
                        String hjdongCode = bjdongToHjdongMap.get(bjdongCode);
                        if (hjdongCode != null) {
                            geocodeCache.put(cacheKey, hjdongCode);
                            return hjdongCode;
                        }
                    }
                }
                // 3차: level1+level2+level4L(법정동명)로 행정동 매핑 시도
                for (JsonNode item : result) {
                    JsonNode structure = item.path("structure");
                    String level1 = structure.path("level1").asText("");
                    String level2 = structure.path("level2").asText("");
                    String level4L = structure.path("level4L").asText("");
                    if (!level1.isBlank() && !level2.isBlank() && !level4L.isBlank()) {
                        String address = level1 + " " + level2 + " " + level4L;
                        var resolved = resolveFromAddress(address);
                        if (resolved != null && resolved.adminLevel() == EAdminLevel.HJDONG) {
                            geocodeCache.put(cacheKey, resolved.code());
                            return resolved.code();
                        }
                    }
                }
            } else {
                log.info("역지오코딩 result 없음 - lon={}, lat={}", longitude, latitude);
            }
            geocodeCache.put(cacheKey, "");
            return null;
        } catch (Exception e) {
            log.info("역지오코딩 예외 - lon={}, lat={}: {}", longitude, latitude, e.getMessage());
            geocodeCache.put(cacheKey, ""); // 실패한 좌표도 캐시하여 재시도 방지
            return null;
        }
    }

    /**
     * 좌표 기반 역지오코딩 결과를 NormalizedRegion으로 반환
     */
    public NormalizedRegion resolveFromCoordinates(double longitude, double latitude) {
        String code = reverseGeocode(longitude, latitude);
        if (code != null) {
            return new NormalizedRegion(code, EAdminLevel.HJDONG);
        }
        return null;
    }

    // 주소 지오코딩 결과 캐시: 도로명주소 → hjdongCode
    private final Map<String, String> addressGeocodeCache = new ConcurrentHashMap<>();

    /**
     * VWorld getCoord API를 이용하여 도로명주소에서 행정동코드 추출.
     * 도로명주소 → 좌표 변환 시 level4AC(행정동코드 10자리) 반환.
     * @param roadAddress 도로명주소 (예: "서울특별시 강남구 테헤란로 123")
     * @return 행정동코드(10자리) 또는 null
     */
    public String geocodeAddress(String roadAddress) {
        if (roadAddress == null || roadAddress.isBlank()) return null;

        // 주소 정규화: 괄호 이전까지만 사용 + 교육청 등 기관명 제거
        String cleanAddress = roadAddress.split("\\(")[0].trim();
        cleanAddress = cleanAddress.replaceAll("교육청", " ").replaceAll("\\s+", " ").trim();
        if (cleanAddress.isBlank()) return null;

        // 시도 명칭 정규화 (VWorld는 공식 시도명만 인식)
        cleanAddress = normalizeSidoInAddress(cleanAddress);

        // 1) type=road 원본 주소
        String result = callVWorldGeocode(cleanAddress, "road");
        if (result != null) return result;

        // 2) type=road 번지 제거 (VWorld는 번지 불일치 시 NOT_FOUND)
        String withoutNumber = cleanAddress.replaceAll("\\s+\\d[\\d-]*\\s*$", "").trim();
        if (!withoutNumber.equals(cleanAddress) && !withoutNumber.isBlank()) {
            result = callVWorldGeocode(withoutNumber, "road");
            if (result != null) return result;
        }

        // 3) 읍면 제거 후 type=road 재시도 (VWorld는 "시군구 도로명"만으로 검색 가능)
        // 예: "경기도 여주시 능서면 마장로" → "경기도 여주시 마장로"
        String withoutEupMyeon = cleanAddress.replaceAll("\\s+\\S+[읍면](?=\\s)", "");
        if (!withoutEupMyeon.equals(cleanAddress) && !withoutEupMyeon.isBlank()) {
            // 번호 포함 시도
            result = callVWorldGeocode(withoutEupMyeon, "road");
            if (result != null) return result;
            // 번호 제거 시도
            String eupMyeonNoNumber = withoutEupMyeon.replaceAll("\\s+\\d[\\d-]*\\s*$", "").trim();
            if (!eupMyeonNoNumber.equals(withoutEupMyeon) && !eupMyeonNoNumber.isBlank()) {
                result = callVWorldGeocode(eupMyeonNoNumber, "road");
                if (result != null) return result;
            }
        }

        // 4) type=parcel 원본 주소 (지번주소 형태일 수 있음)
        result = callVWorldGeocode(cleanAddress, "parcel");
        if (result != null) return result;

        return null;
    }

    /**
     * 주소 문자열의 시도 부분을 VWorld가 인식하는 공식명으로 변환.
     * 예: "전라특별자치도" → "전북특별자치도", "강원도" → "강원특별자치도"
     * 주의: "경기"→"경기도" 같은 약칭은 "경기도..."를 "경기도도..."로 만들 수 있으므로
     * 공백 경계까지 확인하여 정확한 단어만 치환.
     */
    private String normalizeSidoInAddress(String address) {
        String[] parts = address.split("\\s+", 2);
        String sidoPart = parts[0];
        String rest = parts.length > 1 ? " " + parts[1] : "";

        String normalized = SIDO_ALIAS.get(sidoPart);
        if (normalized != null && !normalized.equals(sidoPart)) {
            return normalized + rest;
        }
        return address;
    }

    private String callVWorldGeocode(String address, String type) {
        String cacheKey = type + ":" + address;
        String cached = addressGeocodeCache.get(cacheKey);
        if (cached != null) return cached.isEmpty() ? null : cached;

        try {
            String apiKey = apiProperties.getVworld().getKey();
            if (apiKey == null || apiKey.isBlank()) return null;

            ExchangeStrategies strategies = ExchangeStrategies.builder()
                    .codecs(c -> c.defaultCodecs().maxInMemorySize(1024 * 1024))
                    .build();
            WebClient webClient = webClientBuilder.exchangeStrategies(strategies).build();

            String response = webClient.get()
                    .uri(vworldAddressUrl, uriBuilder -> uriBuilder
                            .queryParam("service", "address")
                            .queryParam("request", "getCoord")
                            .queryParam("version", "2.0")
                            .queryParam("crs", "epsg:4326")
                            .queryParam("address", address)
                            .queryParam("format", "json")
                            .queryParam("type", type)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                addressGeocodeCache.put(cacheKey, "");
                return null;
            }

            JsonNode root = objectMapper.readTree(response);
            String status = root.path("response").path("status").asText("");
            if (!"OK".equals(status)) {
                addressGeocodeCache.put(cacheKey, "");
                return null;
            }

            JsonNode refined = root.path("response").path("refined");
            if (!refined.isMissingNode()) {
                String adminCode = refined.path("structure").path("level4AC").asText("");
                if (!adminCode.isBlank() && adminCode.length() >= 8) {
                    addressGeocodeCache.put(cacheKey, adminCode);
                    return adminCode;
                }
            }

            // parcel type에서 level4AC가 빈 경우: 좌표를 얻어서 역지오코딩 시도
            if ("parcel".equals(type)) {
                JsonNode result = root.path("response").path("result");
                if (!result.isMissingNode()) {
                    JsonNode point = result.path("point");
                    if (!point.isMissingNode()) {
                        try {
                            double x = Double.parseDouble(point.path("x").asText("0"));
                            double y = Double.parseDouble(point.path("y").asText("0"));
                            if (x != 0 && y != 0) {
                                String reverseResult = reverseGeocode(x, y);
                                if (reverseResult != null) {
                                    addressGeocodeCache.put(cacheKey, reverseResult);
                                    return reverseResult;
                                }
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            addressGeocodeCache.put(cacheKey, "");
            return null;
        } catch (Exception e) {
            log.debug("주소 지오코딩 실패 - type={}, address={}: {}", type, address, e.getMessage());
            addressGeocodeCache.put(cacheKey, ""); // 실패한 주소도 캐시하여 재시도 방지
            return null;
        }
    }

    public record NormalizedRegion(String code, EAdminLevel adminLevel) {}
}
