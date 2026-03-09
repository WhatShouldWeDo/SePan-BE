package com.whatshouldwedo.policy.application.port.out;

import com.whatshouldwedo.policy.domain.AiRecommendation;

import java.util.List;

public interface AiRecommendationRepository {

    AiRecommendation save(AiRecommendation recommendation);

    List<AiRecommendation> findAllByRequestedBy(String requestedBy);
}
