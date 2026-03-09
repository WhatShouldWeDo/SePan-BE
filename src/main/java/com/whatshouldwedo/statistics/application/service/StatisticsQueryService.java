package com.whatshouldwedo.statistics.application.service;

import com.whatshouldwedo.statistics.application.port.in.output.result.StatisticsCategoryResult;
import com.whatshouldwedo.statistics.application.port.in.output.result.StatisticsRecordResult;
import com.whatshouldwedo.statistics.application.port.out.StatisticsCategoryRepository;
import com.whatshouldwedo.statistics.application.port.out.StatisticsRecordRepository;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsQueryService {

    private final StatisticsCategoryRepository categoryRepository;
    private final StatisticsRecordRepository recordRepository;
    private final StatisticsVersionMappingService versionMappingService;

    @Transactional(readOnly = true)
    public List<StatisticsCategoryResult> getCategories() {
        return categoryRepository.findAll().stream()
                .map(StatisticsCategoryResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StatisticsRecordResult> getByCategoryAndRegion(
            EStatisticsCategory category, String regionCode, String dataYear) {
        var mappedResults = versionMappingService.findWithVersionMapping(regionCode, category, dataYear);

        return mappedResults.stream()
                .map(r -> r.isMapped()
                        ? StatisticsRecordResult.fromMapped(
                                r.record(), r.mappingType(), r.ratio(), r.sourceHjdongCode())
                        : StatisticsRecordResult.from(r.record())
                ).toList();
    }

    @Transactional(readOnly = true)
    public List<StatisticsRecordResult> getByRegion(String regionCode, String dataYear) {
        return recordRepository.findByRegionCodeAndYear(regionCode, dataYear).stream()
                .map(StatisticsRecordResult::from)
                .toList();
    }
}
