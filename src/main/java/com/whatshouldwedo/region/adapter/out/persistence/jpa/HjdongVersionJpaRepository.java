package com.whatshouldwedo.region.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HjdongVersionJpaRepository extends JpaRepository<HjdongVersionJpaEntity, String> {

    Optional<HjdongVersionJpaEntity> findByIsActiveTrue();

    List<HjdongVersionJpaEntity> findAllByOrderByEffectiveDateDesc();

    boolean existsByVersionName(String versionName);

    Optional<HjdongVersionJpaEntity> findFirstByEffectiveDateLessThanEqualOrderByEffectiveDateDesc(LocalDate date);
}
