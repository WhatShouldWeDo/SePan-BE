package com.whatshouldwedo.statistics.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatisticsDatasetJpaRepository extends JpaRepository<StatisticsDatasetJpaEntity, String> {

    List<StatisticsDatasetJpaEntity> findAllByCategoryId(String categoryId);
}
