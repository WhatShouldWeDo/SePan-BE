package com.whatshouldwedo.statistics.adapter.out.persistence.jpa;

import com.whatshouldwedo.statistics.domain.StatisticsCategory;
import com.whatshouldwedo.statistics.domain.StatisticsCategoryId;
import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import com.whatshouldwedo.statistics.domain.type.ESurveyType;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "statistics_categories")
public class StatisticsCategoryJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "category", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private EStatisticsCategory category;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "data_level", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EAdminLevel dataLevel;

    @Column(name = "survey_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ESurveyType surveyType;

    @Column(name = "unit", length = 30)
    private String unit;

    @Column(name = "source_api_url", length = 500)
    private String sourceApiUrl;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static StatisticsCategoryJpaEntity fromDomain(StatisticsCategory cat) {
        StatisticsCategoryJpaEntity entity = new StatisticsCategoryJpaEntity();
        entity.id = cat.getId().getValue().toString();
        entity.category = cat.getCategory();
        entity.name = cat.getName();
        entity.dataLevel = cat.getDataLevel();
        entity.surveyType = cat.getSurveyType();
        entity.unit = cat.getUnit();
        entity.sourceApiUrl = cat.getSourceApiUrl();
        entity.description = cat.getDescription();
        entity.createdAt = cat.getCreatedAt();
        entity.updatedAt = cat.getUpdatedAt();
        return entity;
    }

    public StatisticsCategory toDomain() {
        return StatisticsCategory.reconstitute(
                StatisticsCategoryId.of(id), category, name, dataLevel, surveyType,
                unit, sourceApiUrl, description, createdAt, updatedAt
        );
    }
}
