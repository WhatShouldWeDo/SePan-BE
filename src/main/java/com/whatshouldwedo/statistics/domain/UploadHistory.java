package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.statistics.domain.type.EUploadStatus;
import com.whatshouldwedo.user.domain.UserId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class UploadHistory {
    private final UploadHistoryId id;
    private final StatisticsDatasetId datasetId;
    private final UserId uploadedBy;
    private final String originalFileName;
    private Long recordCount;
    private EUploadStatus status;
    private String errorMessage;

    private LocalDateTime uploadedAt;

    private UploadHistory(UploadHistoryId id, StatisticsDatasetId datasetId,
                           UserId uploadedBy, String originalFileName) {
        this.id = Objects.requireNonNull(id);
        this.datasetId = Objects.requireNonNull(datasetId);
        this.uploadedBy = Objects.requireNonNull(uploadedBy);
        this.originalFileName = Objects.requireNonNull(originalFileName);
        this.status = EUploadStatus.PROCESSING;
        this.uploadedAt = LocalDateTime.now();
    }

    public static UploadHistory create(UploadHistoryId id, StatisticsDatasetId datasetId,
                                         UserId uploadedBy, String originalFileName) {
        return new UploadHistory(id, datasetId, uploadedBy, originalFileName);
    }

    public static UploadHistory reconstitute(UploadHistoryId id, StatisticsDatasetId datasetId,
                                               UserId uploadedBy, String originalFileName,
                                               Long recordCount, EUploadStatus status,
                                               String errorMessage, LocalDateTime uploadedAt) {
        UploadHistory h = new UploadHistory(id, datasetId, uploadedBy, originalFileName);
        h.recordCount = recordCount;
        h.status = status;
        h.errorMessage = errorMessage;
        h.uploadedAt = uploadedAt;
        return h;
    }

    public void markSuccess(Long recordCount) {
        this.status = EUploadStatus.SUCCESS;
        this.recordCount = recordCount;
    }

    public void markFailed(String errorMessage) {
        this.status = EUploadStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}
