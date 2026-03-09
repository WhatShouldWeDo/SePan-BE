package com.whatshouldwedo.statistics.application.port.in.output.result;

import com.whatshouldwedo.statistics.domain.StatisticsCategory;
import com.whatshouldwedo.region.domain.type.EAdminLevel;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import com.whatshouldwedo.statistics.domain.type.ESurveyType;
import lombok.Getter;

@Getter
public class StatisticsCategoryResult {
    private final String id;
    private final EStatisticsCategory category;
    private final String name;
    private final EAdminLevel dataLevel;
    private final ESurveyType surveyType;
    private final String unit;
    private final String description;

    private StatisticsCategoryResult(String id, EStatisticsCategory category, String name,
                                      EAdminLevel dataLevel, ESurveyType surveyType,
                                      String unit, String description) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.dataLevel = dataLevel;
        this.surveyType = surveyType;
        this.unit = unit;
        this.description = description;
    }

    public static StatisticsCategoryResult from(StatisticsCategory cat) {
        return new StatisticsCategoryResult(
                cat.getId().getValue().toString(),
                cat.getCategory(), cat.getName(), cat.getDataLevel(),
                cat.getSurveyType(), cat.getUnit(), cat.getDescription()
        );
    }
}
