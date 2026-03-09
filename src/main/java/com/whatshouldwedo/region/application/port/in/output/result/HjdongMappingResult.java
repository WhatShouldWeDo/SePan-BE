package com.whatshouldwedo.region.application.port.in.output.result;

import com.whatshouldwedo.region.domain.HjdongMapping;
import com.whatshouldwedo.region.domain.type.EMappingType;
import lombok.Getter;

@Getter
public class HjdongMappingResult {
    private final String id;
    private final String sourceVersionId;
    private final String targetVersionId;
    private final String sourceHjdongCode;
    private final String targetHjdongCode;
    private final EMappingType mappingType;
    private final Double ratio;
    private final String description;

    private HjdongMappingResult(String id, String sourceVersionId, String targetVersionId,
                                 String sourceHjdongCode, String targetHjdongCode,
                                 EMappingType mappingType, Double ratio, String description) {
        this.id = id;
        this.sourceVersionId = sourceVersionId;
        this.targetVersionId = targetVersionId;
        this.sourceHjdongCode = sourceHjdongCode;
        this.targetHjdongCode = targetHjdongCode;
        this.mappingType = mappingType;
        this.ratio = ratio;
        this.description = description;
    }

    public static HjdongMappingResult from(HjdongMapping mapping) {
        return new HjdongMappingResult(
                mapping.getId().getValue().toString(),
                mapping.getSourceVersionId().getValue().toString(),
                mapping.getTargetVersionId().getValue().toString(),
                mapping.getSourceHjdongCode(),
                mapping.getTargetHjdongCode(),
                mapping.getMappingType(),
                mapping.getRatio(),
                mapping.getDescription()
        );
    }
}
