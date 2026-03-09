package com.whatshouldwedo.region.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EAdminLevel {
    SIDO("시도"),
    SIGUNGU("시군구"),
    HJDONG("행정동");

    private final String description;
}
