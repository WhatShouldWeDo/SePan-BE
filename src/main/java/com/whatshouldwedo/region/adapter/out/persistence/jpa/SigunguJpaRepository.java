package com.whatshouldwedo.region.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SigunguJpaRepository extends JpaRepository<SigunguJpaEntity, String> {

    List<SigunguJpaEntity> findAllBySidoId(String sidoId);

    Optional<SigunguJpaEntity> findByCode(String code);

    boolean existsByCode(String code);
}
