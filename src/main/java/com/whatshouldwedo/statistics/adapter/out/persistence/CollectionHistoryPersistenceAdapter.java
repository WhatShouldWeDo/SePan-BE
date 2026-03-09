package com.whatshouldwedo.statistics.adapter.out.persistence;

import com.whatshouldwedo.statistics.adapter.out.persistence.jpa.CollectionHistoryJpaEntity;
import com.whatshouldwedo.statistics.adapter.out.persistence.jpa.CollectionHistoryJpaRepository;
import com.whatshouldwedo.statistics.application.port.out.CollectionHistoryRepository;
import com.whatshouldwedo.statistics.domain.CollectionHistory;
import com.whatshouldwedo.statistics.domain.CollectionHistoryId;
import com.whatshouldwedo.statistics.domain.type.ECollectionStatus;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CollectionHistoryPersistenceAdapter implements CollectionHistoryRepository {

    private final CollectionHistoryJpaRepository jpaRepository;

    @Override
    public CollectionHistory save(CollectionHistory history) {
        return jpaRepository.save(CollectionHistoryJpaEntity.fromDomain(history)).toDomain();
    }

    @Override
    public Optional<CollectionHistory> findById(CollectionHistoryId id) {
        return jpaRepository.findById(id.getValue().toString())
                .map(CollectionHistoryJpaEntity::toDomain);
    }

    @Override
    public List<CollectionHistory> findAll() {
        return jpaRepository.findAllByOrderByStartedAtDesc().stream()
                .map(CollectionHistoryJpaEntity::toDomain).toList();
    }

    @Override
    public Optional<CollectionHistory> findByCategoryAndItemAndYear(
            EStatisticsCategory category, String categoryItemName, String dataYear) {
        return jpaRepository.findByCategoryAndCategoryItemNameAndDataYear(
                category, categoryItemName, dataYear
        ).map(CollectionHistoryJpaEntity::toDomain);
    }

    @Override
    public boolean existsByCategoryAndItemAndYearAndStatus(
            EStatisticsCategory category, String categoryItemName,
            String dataYear, ECollectionStatus status) {
        return jpaRepository.existsByCategoryAndCategoryItemNameAndDataYearAndStatus(
                category, categoryItemName, dataYear, status);
    }
}
