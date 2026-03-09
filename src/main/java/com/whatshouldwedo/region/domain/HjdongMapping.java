package com.whatshouldwedo.region.domain;

import com.whatshouldwedo.region.domain.type.EMappingType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class HjdongMapping {
    private final HjdongMappingId id;
    private final HjdongVersionId sourceVersionId;
    private final HjdongVersionId targetVersionId;
    private final String sourceHjdongCode;
    private final String targetHjdongCode;
    private final EMappingType mappingType;
    private Double ratio;
    private String description;

    private LocalDateTime createdAt;

    private HjdongMapping(HjdongMappingId id, HjdongVersionId sourceVersionId,
                           HjdongVersionId targetVersionId, String sourceHjdongCode,
                           String targetHjdongCode, EMappingType mappingType) {
        this.id = Objects.requireNonNull(id);
        this.sourceVersionId = Objects.requireNonNull(sourceVersionId);
        this.targetVersionId = Objects.requireNonNull(targetVersionId);
        this.sourceHjdongCode = Objects.requireNonNull(sourceHjdongCode);
        this.targetHjdongCode = Objects.requireNonNull(targetHjdongCode);
        this.mappingType = Objects.requireNonNull(mappingType);
        this.createdAt = LocalDateTime.now();
    }

    public static HjdongMapping create(HjdongMappingId id, HjdongVersionId sourceVersionId,
                                         HjdongVersionId targetVersionId, String sourceHjdongCode,
                                         String targetHjdongCode, EMappingType mappingType,
                                         Double ratio, String description) {
        HjdongMapping mapping = new HjdongMapping(id, sourceVersionId, targetVersionId,
                sourceHjdongCode, targetHjdongCode, mappingType);
        mapping.ratio = ratio;
        mapping.description = description;
        return mapping;
    }

    public static HjdongMapping reconstitute(HjdongMappingId id, HjdongVersionId sourceVersionId,
                                               HjdongVersionId targetVersionId, String sourceHjdongCode,
                                               String targetHjdongCode, EMappingType mappingType,
                                               Double ratio, String description,
                                               LocalDateTime createdAt) {
        HjdongMapping mapping = new HjdongMapping(id, sourceVersionId, targetVersionId,
                sourceHjdongCode, targetHjdongCode, mappingType);
        mapping.ratio = ratio;
        mapping.description = description;
        mapping.createdAt = createdAt;
        return mapping;
    }
}
