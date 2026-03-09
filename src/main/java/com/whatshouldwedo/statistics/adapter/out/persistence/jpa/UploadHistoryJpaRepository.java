package com.whatshouldwedo.statistics.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadHistoryJpaRepository extends JpaRepository<UploadHistoryJpaEntity, String> {

    List<UploadHistoryJpaEntity> findAllByOrderByUploadedAtDesc();
}
