package com.whatshouldwedo.region.adapter.out.persistence.jpa;

import com.whatshouldwedo.region.domain.HjdongMapping;
import com.whatshouldwedo.region.domain.HjdongMappingId;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import com.whatshouldwedo.region.domain.type.EMappingType;
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
@Table(name = "hjdong_mappings")
public class HjdongMappingJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "source_version_id", nullable = false, length = 36)
    private String sourceVersionId;

    @Column(name = "target_version_id", nullable = false, length = 36)
    private String targetVersionId;

    @Column(name = "source_hjdong_code", nullable = false, length = 15)
    private String sourceHjdongCode;

    @Column(name = "target_hjdong_code", nullable = false, length = 15)
    private String targetHjdongCode;

    @Column(name = "mapping_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EMappingType mappingType;

    @Column(name = "ratio")
    private Double ratio;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static HjdongMappingJpaEntity fromDomain(HjdongMapping mapping) {
        HjdongMappingJpaEntity entity = new HjdongMappingJpaEntity();
        entity.id = mapping.getId().getValue().toString();
        entity.sourceVersionId = mapping.getSourceVersionId().getValue().toString();
        entity.targetVersionId = mapping.getTargetVersionId().getValue().toString();
        entity.sourceHjdongCode = mapping.getSourceHjdongCode();
        entity.targetHjdongCode = mapping.getTargetHjdongCode();
        entity.mappingType = mapping.getMappingType();
        entity.ratio = mapping.getRatio();
        entity.description = mapping.getDescription();
        entity.createdAt = mapping.getCreatedAt();
        return entity;
    }

    public HjdongMapping toDomain() {
        return HjdongMapping.reconstitute(
                HjdongMappingId.of(id),
                HjdongVersionId.of(sourceVersionId),
                HjdongVersionId.of(targetVersionId),
                sourceHjdongCode,
                targetHjdongCode,
                mappingType,
                ratio,
                description,
                createdAt
        );
    }
}
