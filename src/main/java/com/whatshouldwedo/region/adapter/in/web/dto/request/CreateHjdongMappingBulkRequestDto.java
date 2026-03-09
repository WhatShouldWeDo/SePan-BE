package com.whatshouldwedo.region.adapter.in.web.dto.request;

import java.util.List;

public record CreateHjdongMappingBulkRequestDto(
        List<CreateHjdongMappingRequestDto> mappings
) {
}
