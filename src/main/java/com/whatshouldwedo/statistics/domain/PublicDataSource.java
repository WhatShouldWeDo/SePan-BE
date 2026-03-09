package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.statistics.domain.type.EDataFormat;
import com.whatshouldwedo.statistics.domain.type.EDataProvider;
import com.whatshouldwedo.statistics.domain.type.EDataSourcePriority;
import com.whatshouldwedo.statistics.domain.type.ERegionUnitType;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Getter
public class PublicDataSource {
    private final PublicDataSourceId id;
    private final Integer sourceId;
    private final String name;
    private final EStatisticsCategory category;
    private final EDataProvider provider;
    private String apiUrl;
    private String serviceUrl;
    private Set<EDataFormat> dataFormats;
    private Set<ERegionUnitType> regionUnitTypes;
    private EDataSourcePriority priority;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PublicDataSource(PublicDataSourceId id, Integer sourceId, String name,
                              EStatisticsCategory category, EDataProvider provider) {
        this.id = Objects.requireNonNull(id);
        this.sourceId = Objects.requireNonNull(sourceId);
        this.name = Objects.requireNonNull(name);
        this.category = Objects.requireNonNull(category);
        this.provider = Objects.requireNonNull(provider);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static PublicDataSource create(PublicDataSourceId id, Integer sourceId, String name,
                                           EStatisticsCategory category, EDataProvider provider,
                                           String apiUrl, String serviceUrl,
                                           Set<EDataFormat> dataFormats,
                                           Set<ERegionUnitType> regionUnitTypes,
                                           EDataSourcePriority priority, String notes) {
        PublicDataSource ds = new PublicDataSource(id, sourceId, name, category, provider);
        ds.apiUrl = apiUrl;
        ds.serviceUrl = serviceUrl;
        ds.dataFormats = dataFormats;
        ds.regionUnitTypes = regionUnitTypes;
        ds.priority = priority;
        ds.notes = notes;
        return ds;
    }

    public static PublicDataSource reconstitute(PublicDataSourceId id, Integer sourceId, String name,
                                                 EStatisticsCategory category, EDataProvider provider,
                                                 String apiUrl, String serviceUrl,
                                                 Set<EDataFormat> dataFormats,
                                                 Set<ERegionUnitType> regionUnitTypes,
                                                 EDataSourcePriority priority, String notes,
                                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        PublicDataSource ds = new PublicDataSource(id, sourceId, name, category, provider);
        ds.apiUrl = apiUrl;
        ds.serviceUrl = serviceUrl;
        ds.dataFormats = dataFormats;
        ds.regionUnitTypes = regionUnitTypes;
        ds.priority = priority;
        ds.notes = notes;
        ds.createdAt = createdAt;
        ds.updatedAt = updatedAt;
        return ds;
    }
}
