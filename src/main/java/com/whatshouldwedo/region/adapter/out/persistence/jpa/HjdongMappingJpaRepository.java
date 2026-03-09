package com.whatshouldwedo.region.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HjdongMappingJpaRepository extends JpaRepository<HjdongMappingJpaEntity, String> {

    List<HjdongMappingJpaEntity> findAllBySourceVersionIdAndTargetVersionId(
            String sourceVersionId, String targetVersionId);

    List<HjdongMappingJpaEntity> findAllByTargetVersionId(String targetVersionId);

    List<HjdongMappingJpaEntity> findAllBySourceVersionId(String sourceVersionId);

    List<HjdongMappingJpaEntity> findAllByTargetVersionIdAndTargetHjdongCode(
            String targetVersionId, String targetHjdongCode);
}
