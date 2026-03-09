package com.whatshouldwedo.policy.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiRecommendationJpaRepository extends JpaRepository<AiRecommendationJpaEntity, String> {

    List<AiRecommendationJpaEntity> findAllByRequestedByOrderByCreatedAtDesc(String requestedBy);
}
