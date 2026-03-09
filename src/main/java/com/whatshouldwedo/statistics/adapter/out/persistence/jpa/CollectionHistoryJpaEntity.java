package com.whatshouldwedo.statistics.adapter.out.persistence.jpa;

import com.whatshouldwedo.statistics.domain.CollectionHistory;
import com.whatshouldwedo.statistics.domain.CollectionHistoryId;
import com.whatshouldwedo.statistics.domain.type.ECollectionStatus;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
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
@Table(name = "collection_histories")
public class CollectionHistoryJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "category", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private EStatisticsCategory category;

    @Column(name = "category_item_name", nullable = false, length = 100)
    private String categoryItemName;

    @Column(name = "data_year", nullable = false, length = 10)
    private String dataYear;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ECollectionStatus status;

    @Column(name = "record_count")
    private Long recordCount;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public static CollectionHistoryJpaEntity fromDomain(CollectionHistory h) {
        CollectionHistoryJpaEntity entity = new CollectionHistoryJpaEntity();
        entity.id = h.getId().getValue().toString();
        entity.category = h.getCategory();
        entity.categoryItemName = h.getCategoryItemName();
        entity.dataYear = h.getDataYear();
        entity.status = h.getStatus();
        entity.recordCount = h.getRecordCount();
        entity.errorMessage = h.getErrorMessage();
        entity.retryCount = h.getRetryCount();
        entity.startedAt = h.getStartedAt();
        entity.completedAt = h.getCompletedAt();
        return entity;
    }

    public CollectionHistory toDomain() {
        return CollectionHistory.reconstitute(
                CollectionHistoryId.of(id), category, categoryItemName, dataYear,
                status, recordCount, errorMessage, retryCount, startedAt, completedAt
        );
    }
}
