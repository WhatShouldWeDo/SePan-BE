package com.whatshouldwedo.region.adapter.out.persistence.jpa;

import com.whatshouldwedo.region.domain.HjdongVersion;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hjdong_versions")
public class HjdongVersionJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "version_name", nullable = false, unique = true, length = 30)
    private String versionName;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static HjdongVersionJpaEntity fromDomain(HjdongVersion version) {
        HjdongVersionJpaEntity entity = new HjdongVersionJpaEntity();
        entity.id = version.getId().getValue().toString();
        entity.versionName = version.getVersionName();
        entity.effectiveDate = version.getEffectiveDate();
        entity.isActive = version.isActive();
        entity.description = version.getDescription();
        entity.createdAt = version.getCreatedAt();
        entity.updatedAt = version.getUpdatedAt();
        return entity;
    }

    public HjdongVersion toDomain() {
        return HjdongVersion.reconstitute(
                HjdongVersionId.of(id),
                versionName,
                effectiveDate,
                isActive,
                description,
                createdAt,
                updatedAt
        );
    }
}
