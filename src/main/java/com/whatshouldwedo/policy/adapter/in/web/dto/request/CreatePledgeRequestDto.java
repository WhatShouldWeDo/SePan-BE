package com.whatshouldwedo.policy.adapter.in.web.dto.request;

import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;

public record CreatePledgeRequestDto(
        String title,
        String content,
        String electionId,
        String districtId,
        EStatisticsCategory relatedCategory,
        String regionCode
) {
}
