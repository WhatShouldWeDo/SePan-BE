package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import com.whatshouldwedo.statistics.domain.type.ESurveyType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class StatisticsCategory {
    private final StatisticsCategoryId id;
    private final EStatisticsCategory category;
    private final String name;
    private final EAdminLevel dataLevel;
    private final ESurveyType surveyType;
    private String unit;
    private String sourceApiUrl;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private StatisticsCategory(StatisticsCategoryId id, EStatisticsCategory category,
                                String name, EAdminLevel dataLevel, ESurveyType surveyType) {
        this.id = Objects.requireNonNull(id);
        this.category = Objects.requireNonNull(category);
        this.name = Objects.requireNonNull(name);
        this.dataLevel = Objects.requireNonNull(dataLevel);
        this.surveyType = Objects.requireNonNull(surveyType);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static StatisticsCategory create(StatisticsCategoryId id, EStatisticsCategory category,
                                              String name, EAdminLevel dataLevel, ESurveyType surveyType,
                                              String unit, String sourceApiUrl, String description) {
        StatisticsCategory cat = new StatisticsCategory(id, category, name, dataLevel, surveyType);
        cat.unit = unit;
        cat.sourceApiUrl = sourceApiUrl;
        cat.description = description;
        return cat;
    }

    public static StatisticsCategory reconstitute(StatisticsCategoryId id, EStatisticsCategory category,
                                                    String name, EAdminLevel dataLevel, ESurveyType surveyType,
                                                    String unit, String sourceApiUrl, String description,
                                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        StatisticsCategory cat = new StatisticsCategory(id, category, name, dataLevel, surveyType);
        cat.unit = unit;
        cat.sourceApiUrl = sourceApiUrl;
        cat.description = description;
        cat.createdAt = createdAt;
        cat.updatedAt = updatedAt;
        return cat;
    }
}
