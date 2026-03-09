package com.whatshouldwedo.statistics.application.port.out;

import com.whatshouldwedo.statistics.domain.StatisticsCategory;
import com.whatshouldwedo.statistics.domain.StatisticsCategoryId;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;

import java.util.List;
import java.util.Optional;

public interface StatisticsCategoryRepository {

    StatisticsCategory save(StatisticsCategory category);

    Optional<StatisticsCategory> findById(StatisticsCategoryId id);

    Optional<StatisticsCategory> findByCategory(EStatisticsCategory category);

    List<StatisticsCategory> findAll();

    List<StatisticsCategory> findAllByCategory(EStatisticsCategory category);

    boolean existsByCategoryAndName(EStatisticsCategory category, String name);
}
