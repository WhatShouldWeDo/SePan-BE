package com.whatshouldwedo.policy.application.port.in.output.result;

import com.whatshouldwedo.policy.domain.AiRecommendation;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AiRecommendationResult {
    private final String id;
    private final String regionCode;
    private final EStatisticsCategory relatedCategory;
    private final String reasoning;
    private final String recommendedPledge;
    private final String expectedEffect;
    private final String estimatedBudget;
    private final String sources;
    private final LocalDateTime createdAt;

    private AiRecommendationResult(String id, String regionCode, EStatisticsCategory relatedCategory,
                                     String reasoning, String recommendedPledge, String expectedEffect,
                                     String estimatedBudget, String sources, LocalDateTime createdAt) {
        this.id = id;
        this.regionCode = regionCode;
        this.relatedCategory = relatedCategory;
        this.reasoning = reasoning;
        this.recommendedPledge = recommendedPledge;
        this.expectedEffect = expectedEffect;
        this.estimatedBudget = estimatedBudget;
        this.sources = sources;
        this.createdAt = createdAt;
    }

    public static AiRecommendationResult from(AiRecommendation rec) {
        return new AiRecommendationResult(
                rec.getId().getValue().toString(), rec.getRegionCode(),
                rec.getRelatedCategory(), rec.getReasoning(), rec.getRecommendedPledge(),
                rec.getExpectedEffect(), rec.getEstimatedBudget(), rec.getSources(),
                rec.getCreatedAt()
        );
    }
}
