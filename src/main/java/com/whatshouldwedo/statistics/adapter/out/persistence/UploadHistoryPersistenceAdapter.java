package com.whatshouldwedo.statistics.adapter.out.persistence;

import com.whatshouldwedo.statistics.adapter.out.persistence.jpa.UploadHistoryJpaEntity;
import com.whatshouldwedo.statistics.adapter.out.persistence.jpa.UploadHistoryJpaRepository;
import com.whatshouldwedo.statistics.application.port.out.UploadHistoryRepository;
import com.whatshouldwedo.statistics.domain.UploadHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UploadHistoryPersistenceAdapter implements UploadHistoryRepository {

    private final UploadHistoryJpaRepository uploadHistoryJpaRepository;

    @Override
    public UploadHistory save(UploadHistory history) {
        return uploadHistoryJpaRepository.save(UploadHistoryJpaEntity.fromDomain(history)).toDomain();
    }

    @Override
    public List<UploadHistory> findAll() {
        return uploadHistoryJpaRepository.findAllByOrderByUploadedAtDesc().stream()
                .map(UploadHistoryJpaEntity::toDomain).toList();
    }
}
