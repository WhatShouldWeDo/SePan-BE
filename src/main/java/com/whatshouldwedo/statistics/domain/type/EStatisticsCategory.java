package com.whatshouldwedo.statistics.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EStatisticsCategory {
    ELECTION_ANALYSIS("선거분석"),
    VOTER_INFO("유권자 정보"),
    ECONOMY("재경"),
    HOUSING("주거·부동산"),
    TRANSPORT("교통"),
    SAFETY("사회안전"),
    WELFARE("복지·분배"),
    CULTURE("문화·여가"),
    LOW_BIRTH_AGING("저출생·고령화"),
    EDUCATION("교육·훈련"),
    ENVIRONMENT("환경");

    private final String description;
}
