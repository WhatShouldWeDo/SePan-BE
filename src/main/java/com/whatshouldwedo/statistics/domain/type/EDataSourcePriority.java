package com.whatshouldwedo.statistics.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EDataSourcePriority {
    PRIORITY("선순위"),
    CONSIDERING("고민"),
    WAITING("대기"),
    COMPLETED("완료");

    private final String description;
}
