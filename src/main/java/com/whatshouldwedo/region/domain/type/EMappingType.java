package com.whatshouldwedo.region.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EMappingType {
    RENAMED("이름 변경"),
    MERGED("통합"),
    SPLIT("분리"),
    REORGANIZED("재편"),
    NEW("신설"),
    ABOLISHED("폐지"),
    UNCHANGED("변경 없음");

    private final String description;
}
