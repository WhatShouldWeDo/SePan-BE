package com.whatshouldwedo.election.adapter.in.web.dto.request;

import com.whatshouldwedo.election.domain.type.EElectoralDistrictType;

public record CreateElectoralDistrictRequestDto(
        String name,
        EElectoralDistrictType districtType,
        String sidoCode,
        String sigunguCode
) {
}
