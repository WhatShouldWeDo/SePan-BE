package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.statistics.domain.type.EDataCollectionType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class StatisticsDataset {
    private final StatisticsDatasetId id;
    private final StatisticsCategoryId categoryId;
    private final String dataYear;
    private final EDataCollectionType collectionType;
    private Long recordCount;
    private String mongoCollectionName;
    private LocalDateTime collectedAt;

    private LocalDateTime createdAt;

    private StatisticsDataset(StatisticsDatasetId id, StatisticsCategoryId categoryId,
                               String dataYear, EDataCollectionType collectionType) {
        this.id = Objects.requireNonNull(id);
        this.categoryId = Objects.requireNonNull(categoryId);
        this.dataYear = Objects.requireNonNull(dataYear);
        this.collectionType = Objects.requireNonNull(collectionType);
        this.createdAt = LocalDateTime.now();
    }

    public static StatisticsDataset create(StatisticsDatasetId id, StatisticsCategoryId categoryId,
                                             String dataYear, EDataCollectionType collectionType,
                                             String mongoCollectionName) {
        StatisticsDataset ds = new StatisticsDataset(id, categoryId, dataYear, collectionType);
        ds.mongoCollectionName = mongoCollectionName;
        return ds;
    }

    public static StatisticsDataset reconstitute(StatisticsDatasetId id, StatisticsCategoryId categoryId,
                                                   String dataYear, EDataCollectionType collectionType,
                                                   Long recordCount, String mongoCollectionName,
                                                   LocalDateTime collectedAt, LocalDateTime createdAt) {
        StatisticsDataset ds = new StatisticsDataset(id, categoryId, dataYear, collectionType);
        ds.recordCount = recordCount;
        ds.mongoCollectionName = mongoCollectionName;
        ds.collectedAt = collectedAt;
        ds.createdAt = createdAt;
        return ds;
    }

    public void updateRecordCount(Long count) {
        this.recordCount = count;
        this.collectedAt = LocalDateTime.now();
    }
}
