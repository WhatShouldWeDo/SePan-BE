package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.region.application.port.out.HjdongMappingRepository;
import com.whatshouldwedo.region.application.port.out.HjdongRepository;
import com.whatshouldwedo.region.application.port.out.HjdongVersionRepository;
import com.whatshouldwedo.region.application.port.out.SigunguRepository;
import com.whatshouldwedo.region.domain.Hjdong;
import com.whatshouldwedo.region.domain.HjdongId;
import com.whatshouldwedo.region.domain.HjdongMapping;
import com.whatshouldwedo.region.domain.HjdongMappingId;
import com.whatshouldwedo.region.domain.HjdongVersion;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import com.whatshouldwedo.region.domain.Sigungu;
import com.whatshouldwedo.region.domain.SigunguId;
import com.whatshouldwedo.region.domain.type.EMappingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class HjdongDataInitializer implements ApplicationRunner {

    private final HjdongVersionRepository hjdongVersionRepository;
    private final HjdongRepository hjdongRepository;
    private final HjdongMappingRepository hjdongMappingRepository;
    private final SigunguRepository sigunguRepository;

    private static final int START_YEAR = 2022;
    private static final int END_YEAR = 2026;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("행정동 마스터 데이터 초기화 시작...");

        Map<String, SigunguId> sigunguCodeMap = sigunguRepository.findAll().stream()
                .collect(Collectors.toMap(Sigungu::getCode, Sigungu::getId));

        if (sigunguCodeMap.isEmpty()) {
            log.warn("시군구 데이터가 없습니다. RegionInitializer가 먼저 실행되었는지 확인하세요.");
            return;
        }

        // KOSIS 시군구코드 → Admin 시군구코드 매핑 로딩
        Map<String, String> kosisToAdminMap = loadKosisAdminMapping();
        log.info("KOSIS→Admin 시군구 매핑 로딩 완료 - {}건", kosisToAdminMap.size());

        List<HjdongVersion> createdVersions = new ArrayList<>();
        for (int year = START_YEAR; year <= END_YEAR; year++) {
            HjdongVersion version = createVersionIfNotExists(year);
            if (version != null) {
                loadHjdongCsv(version, sigunguCodeMap, kosisToAdminMap, year);
                createdVersions.add(version);
            }
        }

        // 연도간 매핑 로딩
        List<HjdongVersion> allVersions = hjdongVersionRepository.findAll();
        Map<String, HjdongVersion> versionByName = allVersions.stream()
                .collect(Collectors.toMap(HjdongVersion::getVersionName, v -> v));

        for (int year = START_YEAR; year < END_YEAR; year++) {
            loadMappingCsv(versionByName, year, year + 1);
        }

        // 법정동-행정동 매핑 로딩 (별도 서비스에서 참조)
        log.info("행정동 마스터 데이터 초기화 완료");
    }

    private HjdongVersion createVersionIfNotExists(int year) {
        String versionName = year + "년 기준";
        if (hjdongVersionRepository.existsByVersionName(versionName)) {
            log.info("행정동 버전 이미 존재 - {}", versionName);
            return hjdongVersionRepository.findAll().stream()
                    .filter(v -> v.getVersionName().equals(versionName))
                    .findFirst()
                    .orElse(null);
        }

        HjdongVersion version = HjdongVersion.create(
                HjdongVersionId.generate(),
                versionName,
                LocalDate.of(year, 1, 1),
                year + "년 행정안전부 행정표준코드 기준"
        );

        if (year == END_YEAR) {
            version.activate();
        }

        version = hjdongVersionRepository.save(version);
        log.info("행정동 버전 생성 - {} (active={})", versionName, version.isActive());
        return version;
    }

    private void loadHjdongCsv(HjdongVersion version, Map<String, SigunguId> sigunguCodeMap,
                               Map<String, String> kosisToAdminMap, int year) {
        String csvPath = "data/hjdong/hjdong_" + year + ".csv";
        ClassPathResource resource = new ClassPathResource(csvPath);

        if (!resource.exists()) {
            log.info("행정동 CSV 없음 (스킵) - {}", csvPath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            List<Hjdong> hjdongs = new ArrayList<>();
            Set<String> seenCodes = new HashSet<>();
            String line;
            boolean isHeader = true;
            int unmatchedCount = 0;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 3) continue;

                String code = parts[0].trim();
                String name = parts[1].trim();
                String sigunguCode = parts[2].trim();

                // CSV 내 중복 및 DB 기존 데이터 스킵
                if (!seenCodes.add(code)) continue;
                if (hjdongRepository.existsByCodeAndVersionId(code, version.getId())) {
                    continue;
                }

                // 1차: 원본 sigunguCode로 조회
                SigunguId sigunguId = sigunguCodeMap.get(sigunguCode);
                // 2차: KOSIS→Admin 매핑으로 변환 후 조회
                if (sigunguId == null) {
                    String adminCode = kosisToAdminMap.get(sigunguCode);
                    if (adminCode != null) {
                        sigunguId = sigunguCodeMap.get(adminCode);
                    }
                }
                if (sigunguId == null) {
                    unmatchedCount++;
                    if (unmatchedCount <= 5) {
                        log.warn("매칭되는 시군구 없음 - code={}, sigunguCode={}", code, sigunguCode);
                    }
                    continue;
                }

                hjdongs.add(Hjdong.create(
                        HjdongId.generate(), sigunguId, version.getId(), code, name
                ));
            }

            if (!hjdongs.isEmpty()) {
                hjdongRepository.saveAll(hjdongs);
                log.info("행정동 {} 적재 완료 - {}건", year, hjdongs.size());
            }
            if (unmatchedCount > 0) {
                log.warn("행정동 {} 시군구 매칭 실패 - {}건", year, unmatchedCount);
            }

        } catch (Exception e) {
            log.error("행정동 CSV 로딩 실패 - {} : {}", csvPath, e.getMessage());
        }
    }

    private Map<String, String> loadKosisAdminMapping() {
        Map<String, String> mapping = new java.util.HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/hjdong/kosis_admin_sigungu_mapping.csv");

        if (!resource.exists()) {
            log.warn("KOSIS→Admin 매핑 CSV 없음");
            return mapping;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    mapping.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (Exception e) {
            log.error("KOSIS→Admin 매핑 CSV 로딩 실패: {}", e.getMessage());
        }

        return mapping;
    }

    private void loadMappingCsv(Map<String, HjdongVersion> versionByName, int sourceYear, int targetYear) {
        String csvPath = "data/hjdong/mapping_" + sourceYear + "_" + targetYear + ".csv";
        ClassPathResource resource = new ClassPathResource(csvPath);

        if (!resource.exists()) {
            log.info("행정동 매핑 CSV 없음 (스킵) - {}", csvPath);
            return;
        }

        HjdongVersion sourceVersion = versionByName.get(sourceYear + "년 기준");
        HjdongVersion targetVersion = versionByName.get(targetYear + "년 기준");

        if (sourceVersion == null || targetVersion == null) {
            log.warn("매핑용 버전 없음 - source={}, target={}", sourceYear, targetYear);
            return;
        }

        // 이미 매핑이 있으면 스킵
        if (!hjdongMappingRepository.findAllBySourceAndTargetVersion(
                sourceVersion.getId(), targetVersion.getId()).isEmpty()) {
            log.info("행정동 매핑 이미 존재 - {}→{}", sourceYear, targetYear);
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            List<HjdongMapping> mappings = new ArrayList<>();
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 3) continue;

                String sourceCode = parts[0].trim();
                String targetCode = parts[1].trim();
                EMappingType mappingType = EMappingType.valueOf(parts[2].trim());
                Double ratio = parts.length > 3 && !parts[3].trim().isEmpty()
                        ? Double.parseDouble(parts[3].trim()) : 1.0;
                String description = parts.length > 4 ? parts[4].trim() : "";

                mappings.add(HjdongMapping.create(
                        HjdongMappingId.generate(),
                        sourceVersion.getId(), targetVersion.getId(),
                        sourceCode, targetCode, mappingType,
                        ratio, description
                ));
            }

            if (!mappings.isEmpty()) {
                hjdongMappingRepository.saveAll(mappings);
                log.info("행정동 매핑 {}→{} 적재 완료 - {}건", sourceYear, targetYear, mappings.size());
            }

        } catch (Exception e) {
            log.error("행정동 매핑 CSV 로딩 실패 - {} : {}", csvPath, e.getMessage());
        }
    }
}
