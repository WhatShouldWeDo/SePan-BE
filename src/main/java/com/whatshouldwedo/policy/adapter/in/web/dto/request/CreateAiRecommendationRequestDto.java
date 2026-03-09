package com.whatshouldwedo.policy.adapter.in.web.dto.request;

import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;

public record CreateAiRecommendationRequestDto(
        String regionCode,
        EStatisticsCategory relatedCategory
) {
}
