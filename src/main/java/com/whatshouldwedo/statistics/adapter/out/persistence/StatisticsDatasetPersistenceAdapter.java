package com.whatshouldwedo.statistics.adapter.out.persistence;

import com.whatshouldwedo.statistics.adapter.out.persistence.jpa.StatisticsDatasetJpaEntity;
import com.whatshouldwedo.statistics.adapter.out.persistence.jpa.StatisticsDatasetJpaRepository;
import com.whatshouldwedo.statistics.application.port.out.StatisticsDatasetRepository;
import com.whatshouldwedo.statistics.domain.StatisticsCategoryId;
import com.whatshouldwedo.statistics.domain.StatisticsDataset;
import com.whatshouldwedo.statistics.domain.StatisticsDatasetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StatisticsDatasetPersistenceAdapter implements StatisticsDatasetRepository {

    private final StatisticsDatasetJpaRepository datasetJpaRepository;

    @Override
    public StatisticsDataset save(StatisticsDataset dataset) {
        return datasetJpaRepository.save(StatisticsDatasetJpaEntity.fromDomain(dataset)).toDomain();
    }

    @Override
    public Optional<StatisticsDataset> findById(StatisticsDatasetId id) {
        return datasetJpaRepository.findById(id.getValue().toString())
                .map(StatisticsDatasetJpaEntity::toDomain);
    }

    @Override
    public List<StatisticsDataset> findAllByCategoryId(StatisticsCategoryId categoryId) {
        return datasetJpaRepository.findAllByCategoryId(categoryId.getValue().toString()).stream()
                .map(StatisticsDatasetJpaEntity::toDomain).toList();
    }
}
