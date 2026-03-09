package com.whatshouldwedo.statistics.adapter.out.persistence.jpa;

import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublicDataSourceJpaRepository extends JpaRepository<PublicDataSourceJpaEntity, String> {

    List<PublicDataSourceJpaEntity> findAllByCategory(EStatisticsCategory category);

    Optional<PublicDataSourceJpaEntity> findBySourceId(Integer sourceId);

    boolean existsBySourceId(Integer sourceId);
}
