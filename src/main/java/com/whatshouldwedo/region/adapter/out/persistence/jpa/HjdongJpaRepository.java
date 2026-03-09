package com.whatshouldwedo.region.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HjdongJpaRepository extends JpaRepository<HjdongJpaEntity, String> {

    List<HjdongJpaEntity> findAllBySigunguIdAndVersionId(String sigunguId, String versionId);

    List<HjdongJpaEntity> findAllByVersionId(String versionId);

    Optional<HjdongJpaEntity> findByCodeAndVersionId(String code, String versionId);
}
