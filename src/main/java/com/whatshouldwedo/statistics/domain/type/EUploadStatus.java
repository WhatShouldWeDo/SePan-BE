package com.whatshouldwedo.statistics.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EUploadStatus {
    PROCESSING("처리 중"),
    SUCCESS("성공"),
    FAILED("실패");

    private final String description;
}
