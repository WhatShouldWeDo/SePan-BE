package com.whatshouldwedo.statistics.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EDataFormat {
    JSON("JSON"),
    CSV("CSV");

    private final String description;
}
