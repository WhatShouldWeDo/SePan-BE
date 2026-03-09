package com.whatshouldwedo.region.adapter.in.web.dto.request;

import java.time.LocalDate;

public record CreateHjdongVersionRequestDto(
        String versionName,
        LocalDate effectiveDate,
        String description
) {
}
