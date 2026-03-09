package com.whatshouldwedo.statistics.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ERegionUnitType {
    SIDO("시도"),
    SIGUNGU("시군구"),
    HAENGJEONGGU("행정구"),
    HJDONG("행정동"),
    ELECTORAL_DISTRICT("선거구"),
    VOTING_DISTRICT("투표구"),
    ADDRESS("주소"),
    COORDINATES("위경도"),
    POLYGON("폴리곤");

    private final String description;
}
