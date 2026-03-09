package com.whatshouldwedo.statistics.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EDataProvider {
    NEC("선거관리위원회"),
    GOV_MINISTRY("공공데이터(부처)"),
    GOV_AGENCY("공공데이터(기관)"),
    KOSIS("공공데이터(KOSIS)"),
    LOCAL_GOV("지자체데이터(광역)");

    private final String description;
}
