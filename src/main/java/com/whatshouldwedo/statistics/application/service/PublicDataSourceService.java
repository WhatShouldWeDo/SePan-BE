package com.whatshouldwedo.statistics.application.service;

import com.whatshouldwedo.statistics.application.port.out.PublicDataSourceRepository;
import com.whatshouldwedo.statistics.domain.PublicDataSource;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicDataSourceService {

    private final PublicDataSourceRepository repository;

    public List<PublicDataSource> getAllDataSources() {
        return repository.findAll();
    }

    public List<PublicDataSource> getDataSourcesByCategory(EStatisticsCategory category) {
        return repository.findAllByCategory(category);
    }
}
