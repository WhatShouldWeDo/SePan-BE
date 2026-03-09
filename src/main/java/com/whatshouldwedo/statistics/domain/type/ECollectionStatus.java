package com.whatshouldwedo.statistics.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ECollectionStatus {
    STARTED("수집 시작"),
    SUCCESS("성공"),
    FAILED("실패"),
    PARTIAL("부분 성공");

    private final String description;
}
