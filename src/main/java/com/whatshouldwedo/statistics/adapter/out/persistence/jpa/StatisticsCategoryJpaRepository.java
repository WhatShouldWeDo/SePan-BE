package com.whatshouldwedo.statistics.adapter.out.persistence.jpa;

import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatisticsCategoryJpaRepository extends JpaRepository<StatisticsCategoryJpaEntity, String> {

    Optional<StatisticsCategoryJpaEntity> findByCategory(EStatisticsCategory category);

    List<StatisticsCategoryJpaEntity> findAllByCategory(EStatisticsCategory category);

    boolean existsByCategoryAndName(EStatisticsCategory category, String name);
}
