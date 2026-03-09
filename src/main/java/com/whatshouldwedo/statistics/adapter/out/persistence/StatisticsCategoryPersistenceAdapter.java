package com.whatshouldwedo.statistics.adapter.out.persistence;

import com.whatshouldwedo.statistics.adapter.out.persistence.jpa.StatisticsCategoryJpaEntity;
import com.whatshouldwedo.statistics.adapter.out.persistence.jpa.StatisticsCategoryJpaRepository;
import com.whatshouldwedo.statistics.application.port.out.StatisticsCategoryRepository;
import com.whatshouldwedo.statistics.domain.StatisticsCategory;
import com.whatshouldwedo.statistics.domain.StatisticsCategoryId;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StatisticsCategoryPersistenceAdapter implements StatisticsCategoryRepository {

    private final StatisticsCategoryJpaRepository categoryJpaRepository;

    @Override
    public StatisticsCategory save(StatisticsCategory category) {
        return categoryJpaRepository.save(StatisticsCategoryJpaEntity.fromDomain(category)).toDomain();
    }

    @Override
    public Optional<StatisticsCategory> findById(StatisticsCategoryId id) {
        return categoryJpaRepository.findById(id.getValue().toString())
                .map(StatisticsCategoryJpaEntity::toDomain);
    }

    @Override
    public Optional<StatisticsCategory> findByCategory(EStatisticsCategory category) {
        return categoryJpaRepository.findByCategory(category)
                .map(StatisticsCategoryJpaEntity::toDomain);
    }

    @Override
    public List<StatisticsCategory> findAll() {
        return categoryJpaRepository.findAll().stream()
                .map(StatisticsCategoryJpaEntity::toDomain).toList();
    }

    @Override
    public List<StatisticsCategory> findAllByCategory(EStatisticsCategory category) {
        return categoryJpaRepository.findAllByCategory(category).stream()
                .map(StatisticsCategoryJpaEntity::toDomain).toList();
    }

    @Override
    public boolean existsByCategoryAndName(EStatisticsCategory category, String name) {
        return categoryJpaRepository.existsByCategoryAndName(category, name);
    }
}
