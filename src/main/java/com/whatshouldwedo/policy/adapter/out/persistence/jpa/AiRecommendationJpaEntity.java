package com.whatshouldwedo.policy.adapter.out.persistence.jpa;

import com.whatshouldwedo.policy.domain.AiRecommendation;
import com.whatshouldwedo.policy.domain.AiRecommendationId;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ai_recommendations")
public class AiRecommendationJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "requested_by", nullable = false, length = 36)
    private String requestedBy;

    @Column(name = "region_code", nullable = false, length = 15)
    private String regionCode;

    @Column(name = "related_category", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private EStatisticsCategory relatedCategory;

    @Column(name = "reasoning", nullable = false, columnDefinition = "TEXT")
    private String reasoning;

    @Column(name = "recommended_pledge", nullable = false, columnDefinition = "TEXT")
    private String recommendedPledge;

    @Column(name = "expected_effect", columnDefinition = "TEXT")
    private String expectedEffect;

    @Column(name = "estimated_budget", length = 200)
    private String estimatedBudget;

    @Column(name = "sources", length = 500)
    private String sources;

    @Column(name = "raw_prompt", columnDefinition = "TEXT")
    private String rawPrompt;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static AiRecommendationJpaEntity fromDomain(AiRecommendation rec) {
        AiRecommendationJpaEntity entity = new AiRecommendationJpaEntity();
        entity.id = rec.getId().getValue().toString();
        entity.requestedBy = rec.getRequestedBy();
        entity.regionCode = rec.getRegionCode();
        entity.relatedCategory = rec.getRelatedCategory();
        entity.reasoning = rec.getReasoning();
        entity.recommendedPledge = rec.getRecommendedPledge();
        entity.expectedEffect = rec.getExpectedEffect();
        entity.estimatedBudget = rec.getEstimatedBudget();
        entity.sources = rec.getSources();
        entity.rawPrompt = rec.getRawPrompt();
        entity.rawResponse = rec.getRawResponse();
        entity.createdAt = rec.getCreatedAt();
        return entity;
    }

    public AiRecommendation toDomain() {
        return AiRecommendation.reconstitute(
                AiRecommendationId.of(id), requestedBy, regionCode, relatedCategory,
                reasoning, recommendedPledge, expectedEffect, estimatedBudget,
                sources, rawPrompt, rawResponse, createdAt
        );
    }
}
