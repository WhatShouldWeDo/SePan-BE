package com.whatshouldwedo.policy.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class AiRecommendationId extends BaseId<UUID> {

    private AiRecommendationId(UUID value) {
        super(value);
    }

    public static AiRecommendationId generate() {
        return new AiRecommendationId(generateUUID());
    }

    public static AiRecommendationId of(UUID value) {
        return new AiRecommendationId(value);
    }

    public static AiRecommendationId of(String value) {
        return new AiRecommendationId(UUID.fromString(value));
    }
}
