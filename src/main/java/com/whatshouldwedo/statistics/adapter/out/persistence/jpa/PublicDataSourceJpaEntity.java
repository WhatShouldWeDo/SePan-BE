package com.whatshouldwedo.statistics.adapter.out.persistence.jpa;

import com.whatshouldwedo.statistics.domain.PublicDataSource;
import com.whatshouldwedo.statistics.domain.PublicDataSourceId;
import com.whatshouldwedo.statistics.domain.type.EDataFormat;
import com.whatshouldwedo.statistics.domain.type.EDataProvider;
import com.whatshouldwedo.statistics.domain.type.EDataSourcePriority;
import com.whatshouldwedo.statistics.domain.type.ERegionUnitType;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "public_data_sources")
public class PublicDataSourceJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "source_id", nullable = false, unique = true)
    private Integer sourceId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "category", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private EStatisticsCategory category;

    @Column(name = "provider", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private EDataProvider provider;

    @Column(name = "api_url", length = 500)
    private String apiUrl;

    @Column(name = "service_url", length = 500)
    private String serviceUrl;

    @Column(name = "data_formats", length = 100)
    private String dataFormats;

    @Column(name = "region_unit_types", length = 200)
    private String regionUnitTypes;

    @Column(name = "priority", length = 20)
    @Enumerated(EnumType.STRING)
    private EDataSourcePriority priority;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static PublicDataSourceJpaEntity fromDomain(PublicDataSource ds) {
        PublicDataSourceJpaEntity entity = new PublicDataSourceJpaEntity();
        entity.id = ds.getId().getValue().toString();
        entity.sourceId = ds.getSourceId();
        entity.name = ds.getName();
        entity.category = ds.getCategory();
        entity.provider = ds.getProvider();
        entity.apiUrl = ds.getApiUrl();
        entity.serviceUrl = ds.getServiceUrl();
        entity.dataFormats = ds.getDataFormats() != null
                ? ds.getDataFormats().stream().map(Enum::name).collect(Collectors.joining(","))
                : null;
        entity.regionUnitTypes = ds.getRegionUnitTypes() != null
                ? ds.getRegionUnitTypes().stream().map(Enum::name).collect(Collectors.joining(","))
                : null;
        entity.priority = ds.getPriority();
        entity.notes = ds.getNotes();
        entity.createdAt = ds.getCreatedAt();
        entity.updatedAt = ds.getUpdatedAt();
        return entity;
    }

    public PublicDataSource toDomain() {
        Set<EDataFormat> formats = dataFormats != null && !dataFormats.isBlank()
                ? Arrays.stream(dataFormats.split(",")).map(EDataFormat::valueOf).collect(Collectors.toSet())
                : Collections.emptySet();
        Set<ERegionUnitType> units = regionUnitTypes != null && !regionUnitTypes.isBlank()
                ? Arrays.stream(regionUnitTypes.split(",")).map(ERegionUnitType::valueOf).collect(Collectors.toSet())
                : Collections.emptySet();
        return PublicDataSource.reconstitute(
                PublicDataSourceId.of(id), sourceId, name, category, provider,
                apiUrl, serviceUrl, formats, units, priority, notes,
                createdAt, updatedAt
        );
    }
}
