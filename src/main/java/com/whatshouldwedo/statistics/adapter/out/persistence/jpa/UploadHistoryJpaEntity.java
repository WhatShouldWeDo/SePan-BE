package com.whatshouldwedo.statistics.adapter.out.persistence.jpa;

import com.whatshouldwedo.statistics.domain.StatisticsDatasetId;
import com.whatshouldwedo.statistics.domain.UploadHistory;
import com.whatshouldwedo.statistics.domain.UploadHistoryId;
import com.whatshouldwedo.statistics.domain.type.EUploadStatus;
import com.whatshouldwedo.user.domain.UserId;
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
@Table(name = "upload_histories")
public class UploadHistoryJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "dataset_id", nullable = false, length = 36)
    private String datasetId;

    @Column(name = "uploaded_by", nullable = false, length = 36)
    private String uploadedBy;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "record_count")
    private Long recordCount;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EUploadStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    public static UploadHistoryJpaEntity fromDomain(UploadHistory h) {
        UploadHistoryJpaEntity entity = new UploadHistoryJpaEntity();
        entity.id = h.getId().getValue().toString();
        entity.datasetId = h.getDatasetId().getValue().toString();
        entity.uploadedBy = h.getUploadedBy().getValue().toString();
        entity.originalFileName = h.getOriginalFileName();
        entity.recordCount = h.getRecordCount();
        entity.status = h.getStatus();
        entity.errorMessage = h.getErrorMessage();
        entity.uploadedAt = h.getUploadedAt();
        return entity;
    }

    public UploadHistory toDomain() {
        return UploadHistory.reconstitute(
                UploadHistoryId.of(id), StatisticsDatasetId.of(datasetId),
                UserId.of(uploadedBy), originalFileName, recordCount,
                status, errorMessage, uploadedAt
        );
    }
}
