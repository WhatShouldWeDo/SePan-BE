package com.whatshouldwedo.statistics.application.service;

import com.whatshouldwedo.region.application.port.out.HjdongMappingRepository;
import com.whatshouldwedo.region.application.port.out.HjdongVersionRepository;
import com.whatshouldwedo.region.domain.HjdongMapping;
import com.whatshouldwedo.region.domain.HjdongVersion;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import com.whatshouldwedo.region.domain.type.EMappingType;
import com.whatshouldwedo.statistics.application.port.out.StatisticsRecordRepository;
import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsVersionMappingService {

    private final StatisticsRecordRepository statisticsRecordRepository;
    private final HjdongMappingRepository hjdongMappingRepository;
    private final HjdongVersionRepository hjdongVersionRepository;

    public List<MappedStatisticsResult> findWithVersionMapping(
            String regionCode, EStatisticsCategory category, String dataYear) {

        // 1. 현재 활성 버전의 데이터 직접 조회
        HjdongVersion activeVersion = hjdongVersionRepository.findActiveVersion().orElse(null);
        if (activeVersion == null) {
            return List.of();
        }

        List<StatisticsRecord> directRecords = statisticsRecordRepository
                .findByRegionCodeAndCategoryAndYear(regionCode, category, dataYear);

        // 활성 버전 데이터가 있으면 바로 반환
        List<StatisticsRecord> activeVersionRecords = directRecords.stream()
                .filter(r -> activeVersion.getId().getValue().toString().equals(r.getHjdongVersionId()))
                .toList();

        if (!activeVersionRecords.isEmpty()) {
            return activeVersionRecords.stream()
                    .map(r -> MappedStatisticsResult.direct(r))
                    .toList();
        }

        // 2. 매핑 체인으로 역추적
        List<HjdongMapping> mappings = hjdongMappingRepository
                .findAllByTargetVersionIdAndTargetHjdongCode(activeVersion.getId(), regionCode);

        List<MappedStatisticsResult> results = new ArrayList<>();

        for (HjdongMapping mapping : mappings) {
            List<StatisticsRecord> sourceRecords = statisticsRecordRepository
                    .findByRegionCodeAndCategoryAndYear(mapping.getSourceHjdongCode(), category, dataYear);

            for (StatisticsRecord record : sourceRecords) {
                results.add(MappedStatisticsResult.mapped(record, mapping));
            }
        }

        return results;
    }

    public record MappedStatisticsResult(
            StatisticsRecord record,
            boolean isMapped,
            EMappingType mappingType,
            Double ratio,
            String sourceHjdongCode
    ) {
        public static MappedStatisticsResult direct(StatisticsRecord record) {
            return new MappedStatisticsResult(record, false, null, null, null);
        }

        public static MappedStatisticsResult mapped(StatisticsRecord record, HjdongMapping mapping) {
            return new MappedStatisticsResult(
                    record, true, mapping.getMappingType(),
                    mapping.getRatio(), mapping.getSourceHjdongCode()
            );
        }

        public boolean hasRatio() {
            return ratio != null;
        }
    }
}
