package com.whatshouldwedo.region.adapter.in.web.dto.request;

import com.whatshouldwedo.region.domain.type.EMappingType;

public record CreateHjdongMappingRequestDto(
        String sourceVersionId,
        String sourceHjdongCode,
        String targetHjdongCode,
        EMappingType mappingType,
        Double ratio,
        String description
) {
}
