package com.whatshouldwedo.statistics.application.port.in.output.result;

import com.whatshouldwedo.statistics.domain.PublicDataSource;
import com.whatshouldwedo.statistics.domain.type.EDataFormat;
import com.whatshouldwedo.statistics.domain.type.ERegionUnitType;

import java.util.Set;
import java.util.stream.Collectors;

public record PublicDataSourceResult(
        String id,
        Integer sourceId,
        String name,
        String category,
        String categoryDescription,
        String provider,
        String providerDescription,
        String apiUrl,
        String serviceUrl,
        Set<String> dataFormats,
        Set<String> regionUnitTypes,
        String priority,
        String notes
) {
    public static PublicDataSourceResult from(PublicDataSource ds) {
        return new PublicDataSourceResult(
                ds.getId().getValue().toString(),
                ds.getSourceId(),
                ds.getName(),
                ds.getCategory().name(),
                ds.getCategory().getDescription(),
                ds.getProvider().name(),
                ds.getProvider().getDescription(),
                ds.getApiUrl(),
                ds.getServiceUrl(),
                ds.getDataFormats() != null
                        ? ds.getDataFormats().stream().map(EDataFormat::getDescription).collect(Collectors.toSet())
                        : Set.of(),
                ds.getRegionUnitTypes() != null
                        ? ds.getRegionUnitTypes().stream().map(ERegionUnitType::getDescription).collect(Collectors.toSet())
                        : Set.of(),
                ds.getPriority() != null ? ds.getPriority().getDescription() : null,
                ds.getNotes()
        );
    }
}
