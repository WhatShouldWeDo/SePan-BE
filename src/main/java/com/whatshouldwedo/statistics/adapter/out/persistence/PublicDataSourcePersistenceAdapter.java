package com.whatshouldwedo.statistics.adapter.out.persistence;

import com.whatshouldwedo.statistics.adapter.out.persistence.jpa.PublicDataSourceJpaEntity;
import com.whatshouldwedo.statistics.adapter.out.persistence.jpa.PublicDataSourceJpaRepository;
import com.whatshouldwedo.statistics.application.port.out.PublicDataSourceRepository;
import com.whatshouldwedo.statistics.domain.PublicDataSource;
import com.whatshouldwedo.statistics.domain.PublicDataSourceId;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PublicDataSourcePersistenceAdapter implements PublicDataSourceRepository {

    private final PublicDataSourceJpaRepository jpaRepository;

    @Override
    public PublicDataSource save(PublicDataSource dataSource) {
        return jpaRepository.save(PublicDataSourceJpaEntity.fromDomain(dataSource)).toDomain();
    }

    @Override
    public Optional<PublicDataSource> findById(PublicDataSourceId id) {
        return jpaRepository.findById(id.getValue().toString())
                .map(PublicDataSourceJpaEntity::toDomain);
    }

    @Override
    public List<PublicDataSource> findAll() {
        return jpaRepository.findAll().stream()
                .map(PublicDataSourceJpaEntity::toDomain).toList();
    }

    @Override
    public List<PublicDataSource> findAllByCategory(EStatisticsCategory category) {
        return jpaRepository.findAllByCategory(category).stream()
                .map(PublicDataSourceJpaEntity::toDomain).toList();
    }

    @Override
    public boolean existsBySourceId(Integer sourceId) {
        return jpaRepository.existsBySourceId(sourceId);
    }
}
