package com.whatshouldwedo.statistics.application.port.out;

import com.whatshouldwedo.statistics.domain.CollectionHistory;
import com.whatshouldwedo.statistics.domain.CollectionHistoryId;
import com.whatshouldwedo.statistics.domain.type.ECollectionStatus;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;

import java.util.List;
import java.util.Optional;

public interface CollectionHistoryRepository {

    CollectionHistory save(CollectionHistory history);

    Optional<CollectionHistory> findById(CollectionHistoryId id);

    List<CollectionHistory> findAll();

    Optional<CollectionHistory> findByCategoryAndItemAndYear(
            EStatisticsCategory category, String categoryItemName, String dataYear);

    boolean existsByCategoryAndItemAndYearAndStatus(
            EStatisticsCategory category, String categoryItemName,
            String dataYear, ECollectionStatus status);
}
