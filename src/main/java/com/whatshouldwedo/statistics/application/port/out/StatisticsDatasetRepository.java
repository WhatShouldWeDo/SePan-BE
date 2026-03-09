package com.whatshouldwedo.statistics.application.port.out;

import com.whatshouldwedo.statistics.domain.StatisticsCategoryId;
import com.whatshouldwedo.statistics.domain.StatisticsDataset;
import com.whatshouldwedo.statistics.domain.StatisticsDatasetId;

import java.util.List;
import java.util.Optional;

public interface StatisticsDatasetRepository {

    StatisticsDataset save(StatisticsDataset dataset);

    Optional<StatisticsDataset> findById(StatisticsDatasetId id);

    List<StatisticsDataset> findAllByCategoryId(StatisticsCategoryId categoryId);
}
