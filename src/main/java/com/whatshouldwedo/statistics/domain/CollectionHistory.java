package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.statistics.domain.type.ECollectionStatus;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class CollectionHistory {
    private final CollectionHistoryId id;
    private final EStatisticsCategory category;
    private final String categoryItemName;
    private final String dataYear;
    private ECollectionStatus status;
    private Long recordCount;
    private String errorMessage;
    private int retryCount;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private CollectionHistory(CollectionHistoryId id, EStatisticsCategory category,
                               String categoryItemName, String dataYear) {
        this.id = Objects.requireNonNull(id);
        this.category = Objects.requireNonNull(category);
        this.categoryItemName = Objects.requireNonNull(categoryItemName);
        this.dataYear = Objects.requireNonNull(dataYear);
        this.status = ECollectionStatus.STARTED;
        this.retryCount = 0;
        this.startedAt = LocalDateTime.now();
    }

    public static CollectionHistory create(CollectionHistoryId id, EStatisticsCategory category,
                                             String categoryItemName, String dataYear) {
        return new CollectionHistory(id, category, categoryItemName, dataYear);
    }

    public static CollectionHistory reconstitute(CollectionHistoryId id, EStatisticsCategory category,
                                                   String categoryItemName, String dataYear,
                                                   ECollectionStatus status, Long recordCount,
                                                   String errorMessage, int retryCount,
                                                   LocalDateTime startedAt, LocalDateTime completedAt) {
        CollectionHistory h = new CollectionHistory(id, category, categoryItemName, dataYear);
        h.status = status;
        h.recordCount = recordCount;
        h.errorMessage = errorMessage;
        h.retryCount = retryCount;
        h.startedAt = startedAt;
        h.completedAt = completedAt;
        return h;
    }

    public void markSuccess(long recordCount) {
        this.status = ECollectionStatus.SUCCESS;
        this.recordCount = recordCount;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String errorMessage) {
        this.status = ECollectionStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }

    public void markPartial(long recordCount, String errorMessage) {
        this.status = ECollectionStatus.PARTIAL;
        this.recordCount = recordCount;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }

    public void incrementRetry() {
        this.retryCount++;
    }

    public void restart() {
        this.status = ECollectionStatus.STARTED;
        this.errorMessage = null;
        this.recordCount = null;
        this.completedAt = null;
        this.startedAt = LocalDateTime.now();
    }
}
