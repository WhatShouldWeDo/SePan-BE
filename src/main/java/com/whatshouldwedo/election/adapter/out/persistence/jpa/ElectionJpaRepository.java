package com.whatshouldwedo.election.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionJpaRepository extends JpaRepository<ElectionJpaEntity, String> {
}
