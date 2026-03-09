package com.whatshouldwedo.policy.domain;

import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class AiRecommendation {
    private final AiRecommendationId id;
    private final String requestedBy;
    private final String regionCode;
    private final EStatisticsCategory relatedCategory;
    private final String reasoning;
    private final String recommendedPledge;
    private String expectedEffect;
    private String estimatedBudget;
    private String sources;
    private String rawPrompt;
    private String rawResponse;

    private LocalDateTime createdAt;

    private AiRecommendation(AiRecommendationId id, String requestedBy, String regionCode,
                               EStatisticsCategory relatedCategory, String reasoning,
                               String recommendedPledge) {
        this.id = Objects.requireNonNull(id);
        this.requestedBy = Objects.requireNonNull(requestedBy);
        this.regionCode = Objects.requireNonNull(regionCode);
        this.relatedCategory = Objects.requireNonNull(relatedCategory);
        this.reasoning = Objects.requireNonNull(reasoning);
        this.recommendedPledge = Objects.requireNonNull(recommendedPledge);
        this.createdAt = LocalDateTime.now();
    }

    public static AiRecommendation create(AiRecommendationId id, String requestedBy,
                                             String regionCode, EStatisticsCategory relatedCategory,
                                             String reasoning, String recommendedPledge,
                                             String expectedEffect, String estimatedBudget,
                                             String sources, String rawPrompt, String rawResponse) {
        AiRecommendation rec = new AiRecommendation(id, requestedBy, regionCode,
                relatedCategory, reasoning, recommendedPledge);
        rec.expectedEffect = expectedEffect;
        rec.estimatedBudget = estimatedBudget;
        rec.sources = sources;
        rec.rawPrompt = rawPrompt;
        rec.rawResponse = rawResponse;
        return rec;
    }

    public static AiRecommendation reconstitute(AiRecommendationId id, String requestedBy,
                                                   String regionCode, EStatisticsCategory relatedCategory,
                                                   String reasoning, String recommendedPledge,
                                                   String expectedEffect, String estimatedBudget,
                                                   String sources, String rawPrompt, String rawResponse,
                                                   LocalDateTime createdAt) {
        AiRecommendation rec = new AiRecommendation(id, requestedBy, regionCode,
                relatedCategory, reasoning, recommendedPledge);
        rec.expectedEffect = expectedEffect;
        rec.estimatedBudget = estimatedBudget;
        rec.sources = sources;
        rec.rawPrompt = rawPrompt;
        rec.rawResponse = rawResponse;
        rec.createdAt = createdAt;
        return rec;
    }
}
