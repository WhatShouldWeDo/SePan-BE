package com.whatshouldwedo.statistics.adapter.out.persistence.jpa;

import com.whatshouldwedo.statistics.domain.type.ECollectionStatus;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionHistoryJpaRepository extends JpaRepository<CollectionHistoryJpaEntity, String> {

    List<CollectionHistoryJpaEntity> findAllByOrderByStartedAtDesc();

    Optional<CollectionHistoryJpaEntity> findByCategoryAndCategoryItemNameAndDataYear(
            EStatisticsCategory category, String categoryItemName, String dataYear);

    boolean existsByCategoryAndCategoryItemNameAndDataYearAndStatus(
            EStatisticsCategory category, String categoryItemName,
            String dataYear, ECollectionStatus status);
}
