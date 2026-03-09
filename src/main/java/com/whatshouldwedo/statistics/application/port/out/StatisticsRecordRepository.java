package com.whatshouldwedo.statistics.application.port.out;

import com.whatshouldwedo.statistics.domain.StatisticsRecord;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;

import java.util.List;

public interface StatisticsRecordRepository {

    StatisticsRecord save(StatisticsRecord record);

    List<StatisticsRecord> saveAll(List<StatisticsRecord> records);

    List<StatisticsRecord> findByRegionCodeAndCategoryAndYear(
            String regionCode, EStatisticsCategory category, String dataYear);

    List<StatisticsRecord> findByRegionCodeAndYear(String regionCode, String dataYear);

    List<StatisticsRecord> findByHjdongVersionIdAndRegionCode(
            String hjdongVersionId, String regionCode);

    List<StatisticsRecord> findByCategoryAndYear(EStatisticsCategory category, String dataYear);

    List<StatisticsRecord> findByRegionCodesAndCategoryAndYear(
            List<String> regionCodes, EStatisticsCategory category, String dataYear);

    long deleteByCategoryAndCategoryItemAndYear(
            EStatisticsCategory category, String categoryItemName, String dataYear);
}
