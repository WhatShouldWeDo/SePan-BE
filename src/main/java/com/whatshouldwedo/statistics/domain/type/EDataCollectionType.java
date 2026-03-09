package com.whatshouldwedo.statistics.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EDataCollectionType {
    API("공공 API"),
    CSV_UPLOAD("CSV 업로드"),
    EXCEL_UPLOAD("엑셀 업로드"),
    MANUAL("수동 입력");

    private final String description;
}
