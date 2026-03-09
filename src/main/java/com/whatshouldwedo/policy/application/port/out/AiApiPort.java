package com.whatshouldwedo.policy.application.port.out;

public interface AiApiPort {

    AiApiResponse requestRecommendation(String prompt);

    record AiApiResponse(
            String reasoning,
            String recommendedPledge,
            String expectedEffect,
            String estimatedBudget,
            String sources,
            String rawResponse
    ) {
    }
}
