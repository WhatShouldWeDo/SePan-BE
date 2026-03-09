package com.whatshouldwedo.policy.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ERecommendationSource {
    REGIONAL_STATS("지역 통계"),
    COMPLAINT("민원"),
    POLL("여론조사"),
    SNS("SNS"),
    NEWS("뉴스");

    private final String description;
}
