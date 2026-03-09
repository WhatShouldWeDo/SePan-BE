package com.whatshouldwedo.statistics.application.port.out;

import com.whatshouldwedo.statistics.domain.PublicDataSource;
import com.whatshouldwedo.statistics.domain.PublicDataSourceId;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;

import java.util.List;
import java.util.Optional;

public interface PublicDataSourceRepository {

    PublicDataSource save(PublicDataSource dataSource);

    Optional<PublicDataSource> findById(PublicDataSourceId id);

    List<PublicDataSource> findAll();

    List<PublicDataSource> findAllByCategory(EStatisticsCategory category);

    boolean existsBySourceId(Integer sourceId);
}
