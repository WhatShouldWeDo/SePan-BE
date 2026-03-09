package com.whatshouldwedo.statistics.application.service;

import com.whatshouldwedo.region.application.port.out.HjdongVersionRepository;
import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.application.port.out.CollectionHistoryRepository;
import com.whatshouldwedo.statistics.application.port.out.PublicDataCollector;
import com.whatshouldwedo.statistics.application.port.out.StatisticsRecordRepository;
import com.whatshouldwedo.statistics.domain.CollectionHistory;
import com.whatshouldwedo.statistics.domain.CollectionHistoryId;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.ECollectionStatus;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataCollectionService {

    private final List<PublicDataCollector> collectors;
    private final StatisticsRecordRepository statisticsRecordRepository;
    private final CollectionHistoryRepository collectionHistoryRepository;
    private final HjdongVersionRepository hjdongVersionRepository;
    private final RegionCodeResolver regionCodeResolver;

    @Async
    public void collectAll(String dataYear, boolean force) {
        log.info("=== 전체 데이터 수집 시작 - year={}, force={} ===", dataYear, force);

        for (PublicDataCollector collector : collectors) {
            collectSingle(collector, dataYear, force);
        }

        log.info("=== 전체 데이터 수집 완료 ===");
    }

    public Map<String, Object> collectByCategory(EStatisticsCategory category, String dataYear, boolean force) {
        List<PublicDataCollector> targetCollectors = collectors.stream()
                .filter(c -> c.getCategory() == category)
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("category", category.name());
        result.put("categoryDescription", category.getDescription());
        result.put("dataYear", dataYear);

        List<Map<String, Object>> itemResults = new ArrayList<>();
        long totalRecords = 0;

        for (PublicDataCollector collector : targetCollectors) {
            CollectionResult cr = collectSingle(collector, dataYear, force);
            Map<String, Object> itemResult = new HashMap<>();
            itemResult.put("itemName", collector.getCategoryItemName());
            itemResult.put("status", cr.status().name());
            itemResult.put("recordCount", cr.recordCount());
            if (cr.errorMessage() != null) {
                itemResult.put("error", cr.errorMessage());
            }
            itemResults.add(itemResult);
            totalRecords += cr.recordCount();
        }

        result.put("items", itemResults);
        result.put("totalRecords", totalRecords);
        return result;
    }

    public Map<String, Object> collectByItem(EStatisticsCategory category, String itemName,
                                               String dataYear, boolean force) {
        PublicDataCollector collector = collectors.stream()
                .filter(c -> c.getCategory() == category
                        && c.getCategoryItemName().equals(itemName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "수집기를 찾을 수 없습니다: " + category + "/" + itemName));

        CollectionResult cr = collectSingle(collector, dataYear, force);

        Map<String, Object> result = new HashMap<>();
        result.put("category", category.name());
        result.put("itemName", itemName);
        result.put("dataYear", dataYear);
        result.put("status", cr.status().name());
        result.put("recordCount", cr.recordCount());
        if (cr.errorMessage() != null) {
            result.put("error", cr.errorMessage());
        }
        return result;
    }

    /**
     * 멀티연도 수집: startYear ~ endYear 범위에서 연도별 순차 실행
     */
    @Async
    public void collectMultiYear(int startYear, int endYear, boolean force) {
        log.info("=== 멀티연도 데이터 수집 시작 - {}~{}, force={} ===", startYear, endYear, force);

        for (int year = startYear; year <= endYear; year++) {
            String dataYear = String.valueOf(year);
            log.info("--- {}년 데이터 수집 시작 ---", year);
            for (PublicDataCollector collector : collectors) {
                collectSingle(collector, dataYear, force);
            }
            log.info("--- {}년 데이터 수집 완료 ---", year);
        }

        log.info("=== 멀티연도 데이터 수집 완료 - {}~{} ===", startYear, endYear);
    }

    public List<Map<String, String>> listCollectors() {
        return collectors.stream()
                .map(c -> Map.of(
                        "category", c.getCategory().name(),
                        "categoryDescription", c.getCategory().getDescription(),
                        "itemName", c.getCategoryItemName()
                ))
                .toList();
    }

    private CollectionResult collectSingle(PublicDataCollector collector, String dataYear, boolean force) {
        EStatisticsCategory category = collector.getCategory();
        String itemName = collector.getCategoryItemName();

        // 1. 중복 체크 - 이미 성공한 수집이 있으면 스킵 (force=true면 무시)
        if (!force && collectionHistoryRepository.existsByCategoryAndItemAndYearAndStatus(
                category, itemName, dataYear, ECollectionStatus.SUCCESS)) {
            log.info("[SKIP] 이미 수집 완료 - {}/{} year={}", category, itemName, dataYear);
            return new CollectionResult(ECollectionStatus.SUCCESS, 0, "이미 수집 완료 (스킵)");
        }

        // 2. 기존 이력 조회 또는 새로 생성
        CollectionHistory history = collectionHistoryRepository
                .findByCategoryAndItemAndYear(category, itemName, dataYear)
                .map(h -> {
                    h.incrementRetry();
                    h.restart();
                    return h;
                })
                .orElse(CollectionHistory.create(
                        CollectionHistoryId.generate(), category, itemName, dataYear));
        history = collectionHistoryRepository.save(history);

        // 3. 재시도 포함 수집
        int maxRetries = collector.getMaxRetries();
        long retryDelay = collector.getRetryDelayMs();
        List<StatisticsRecord> records = null;
        Exception lastError = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    log.info("[RETRY] {}/{} - attempt {}/{}", category, itemName, attempt, maxRetries);
                    Thread.sleep(retryDelay * attempt);
                }
                records = collector.collect(dataYear);
                lastError = null;
                break;
            } catch (Exception e) {
                lastError = e;
                log.warn("[FAIL] {}/{} - attempt {}/{} - {}",
                        category, itemName, attempt, maxRetries, e.getMessage());
            }
        }

        // 4. 결과 처리
        if (lastError != null) {
            history.markFailed(lastError.getMessage());
            collectionHistoryRepository.save(history);
            return new CollectionResult(ECollectionStatus.FAILED, 0, lastError.getMessage());
        }

        if (records == null || records.isEmpty()) {
            history.markSuccess(0);
            collectionHistoryRepository.save(history);
            return new CollectionResult(ECollectionStatus.SUCCESS, 0, null);
        }

        // 5. 후처리: hjdongVersionId 주입 + regionCode 정규화
        records = postProcess(records, dataYear);

        // 6. 기존 데이터 삭제 후 저장 (중복 방지)
        try {
            long deleted = statisticsRecordRepository.deleteByCategoryAndCategoryItemAndYear(
                    category, itemName, dataYear);
            if (deleted > 0) {
                log.info("[CLEANUP] 기존 데이터 {}건 삭제 - {}/{} year={}",
                        deleted, category, itemName, dataYear);
            }

            List<StatisticsRecord> saved = statisticsRecordRepository.saveAll(records);
            history.markSuccess(saved.size());
            collectionHistoryRepository.save(history);
            return new CollectionResult(ECollectionStatus.SUCCESS, saved.size(), null);
        } catch (Exception e) {
            log.error("[SAVE ERROR] {}/{} - {}", category, itemName, e.getMessage(), e);
            history.markPartial(0, "저장 실패: " + e.getMessage());
            collectionHistoryRepository.save(history);
            return new CollectionResult(ECollectionStatus.PARTIAL, 0, e.getMessage());
        }
    }

    /**
     * 수집된 레코드에 hjdongVersionId를 주입하고 regionCode를 정규화.
     * 한 collector 내에서 adminLevel이 섞이지 않도록 보장.
     */
    private List<StatisticsRecord> postProcess(List<StatisticsRecord> records, String dataYear) {
        String hjdongVersionId = resolveVersionIdForYear(dataYear);

        List<StatisticsRecord> processed = new ArrayList<>(records.size());

        for (StatisticsRecord record : records) {
            // regionCode 정규화
            RegionCodeResolver.NormalizedRegion normalized =
                    regionCodeResolver.normalize(record.getRegionCode(), record.getAdminLevel());

            // 원본 정보를 data에 보존
            Map<String, Object> enrichedData = new HashMap<>(record.getData());
            if (!record.getRegionCode().equals(normalized.code())) {
                enrichedData.put("original_region_code", record.getRegionCode());
            }
            if (record.getAdminLevel() != normalized.adminLevel()) {
                enrichedData.put("original_admin_level", record.getAdminLevel().name());
            }

            processed.add(StatisticsRecord.create(
                    record.getDatasetId(),
                    record.getCategoryCode(),
                    normalized.code(),
                    normalized.adminLevel(),
                    hjdongVersionId,
                    record.getDataYear(),
                    enrichedData
            ));
        }

        // adminLevel 혼합 방지: 한 collector 내에서 레벨이 섞이면 다수결로 통일
        enforceConsistentAdminLevel(processed);

        return processed;
    }

    /**
     * 한 batch 내에서 adminLevel 혼합 상태를 로깅만 수행.
     * 데이터 누락 방지를 위해 레코드를 절대 제거하거나 하향하지 않음.
     */
    private void enforceConsistentAdminLevel(List<StatisticsRecord> records) {
        if (records.isEmpty()) return;

        Map<EAdminLevel, Long> levelCounts = new HashMap<>();
        for (StatisticsRecord r : records) {
            levelCounts.merge(r.getAdminLevel(), 1L, Long::sum);
        }

        if (levelCounts.size() <= 1) return; // 이미 통일됨

        long totalCount = records.size();
        log.warn("[PostProcess] adminLevel 혼합 감지 - 총 {}건: {}", totalCount, levelCounts);
    }

    /**
     * dataYear → effectiveDate ≤ 해당 연도 1/1인 최신 버전의 ID
     */
    private String resolveVersionIdForYear(String dataYear) {
        try {
            int year = Integer.parseInt(dataYear.length() >= 4 ? dataYear.substring(0, 4) : dataYear);
            LocalDate targetDate = LocalDate.of(year, 1, 1);

            return hjdongVersionRepository.findLatestByEffectiveDateLessThanEqual(targetDate)
                    .map(v -> v.getId().getValue().toString())
                    .orElseGet(() -> {
                        // fallback: 활성 버전 사용
                        return hjdongVersionRepository.findActiveVersion()
                                .map(v -> v.getId().getValue().toString())
                                .orElse(null);
                    });
        } catch (NumberFormatException e) {
            log.warn("dataYear 파싱 실패 - dataYear={}", dataYear);
            return hjdongVersionRepository.findActiveVersion()
                    .map(v -> v.getId().getValue().toString())
                    .orElse(null);
        }
    }

    private record CollectionResult(ECollectionStatus status, long recordCount, String errorMessage) {
    }
}
