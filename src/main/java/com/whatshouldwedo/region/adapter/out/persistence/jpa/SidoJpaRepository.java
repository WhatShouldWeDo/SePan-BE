package com.whatshouldwedo.region.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SidoJpaRepository extends JpaRepository<SidoJpaEntity, String> {

    Optional<SidoJpaEntity> findByCode(String code);

    boolean existsByCode(String code);
}
