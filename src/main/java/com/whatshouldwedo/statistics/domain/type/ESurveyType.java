package com.whatshouldwedo.statistics.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ESurveyType {
    CENSUS("전수 조사"),
    SAMPLE("표본 조사");

    private final String description;
}
