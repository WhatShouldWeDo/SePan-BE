package com.whatshouldwedo.statistics.adapter.out.persistence.jpa;

import com.whatshouldwedo.statistics.domain.StatisticsCategoryId;
import com.whatshouldwedo.statistics.domain.StatisticsDataset;
import com.whatshouldwedo.statistics.domain.StatisticsDatasetId;
import com.whatshouldwedo.statistics.domain.type.EDataCollectionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "statistics_datasets")
public class StatisticsDatasetJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "category_id", nullable = false, length = 36)
    private String categoryId;

    @Column(name = "data_year", nullable = false, length = 4)
    private String dataYear;

    @Column(name = "collection_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private EDataCollectionType collectionType;

    @Column(name = "record_count")
    private Long recordCount;

    @Column(name = "mongo_collection_name", length = 100)
    private String mongoCollectionName;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static StatisticsDatasetJpaEntity fromDomain(StatisticsDataset ds) {
        StatisticsDatasetJpaEntity entity = new StatisticsDatasetJpaEntity();
        entity.id = ds.getId().getValue().toString();
        entity.categoryId = ds.getCategoryId().getValue().toString();
        entity.dataYear = ds.getDataYear();
        entity.collectionType = ds.getCollectionType();
        entity.recordCount = ds.getRecordCount();
        entity.mongoCollectionName = ds.getMongoCollectionName();
        entity.collectedAt = ds.getCollectedAt();
        entity.createdAt = ds.getCreatedAt();
        return entity;
    }

    public StatisticsDataset toDomain() {
        return StatisticsDataset.reconstitute(
                StatisticsDatasetId.of(id), StatisticsCategoryId.of(categoryId),
                dataYear, collectionType, recordCount, mongoCollectionName,
                collectedAt, createdAt
        );
    }
}
