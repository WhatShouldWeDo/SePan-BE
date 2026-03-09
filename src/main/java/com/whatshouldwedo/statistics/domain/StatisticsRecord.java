package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Getter
public class StatisticsRecord {
    private final String id;
    private final String datasetId;
    private final EStatisticsCategory categoryCode;
    private final String regionCode;
    private final EAdminLevel adminLevel;
    private final String hjdongVersionId;
    private final String dataYear;
    private final Map<String, Object> data;

    private LocalDateTime createdAt;

    private StatisticsRecord(String id, String datasetId, EStatisticsCategory categoryCode,
                              String regionCode, EAdminLevel adminLevel, String hjdongVersionId,
                              String dataYear, Map<String, Object> data) {
        this.id = id;
        this.datasetId = datasetId; // API 직접 수집 시 datasetId 없이 생성 가능
        this.categoryCode = Objects.requireNonNull(categoryCode);
        this.regionCode = Objects.requireNonNull(regionCode);
        this.adminLevel = Objects.requireNonNull(adminLevel);
        this.hjdongVersionId = hjdongVersionId;
        this.dataYear = Objects.requireNonNull(dataYear);
        this.data = Objects.requireNonNull(data);
        this.createdAt = LocalDateTime.now();
    }

    public static StatisticsRecord create(String datasetId, EStatisticsCategory categoryCode,
                                            String regionCode, EAdminLevel adminLevel,
                                            String hjdongVersionId, String dataYear,
                                            Map<String, Object> data) {
        return new StatisticsRecord(null, datasetId, categoryCode, regionCode,
                adminLevel, hjdongVersionId, dataYear, data);
    }

    public static StatisticsRecord reconstitute(String id, String datasetId, EStatisticsCategory categoryCode,
                                                  String regionCode, EAdminLevel adminLevel,
                                                  String hjdongVersionId, String dataYear,
                                                  Map<String, Object> data, LocalDateTime createdAt) {
        StatisticsRecord record = new StatisticsRecord(id, datasetId, categoryCode,
                regionCode, adminLevel, hjdongVersionId, dataYear, data);
        record.createdAt = createdAt;
        return record;
    }
}
