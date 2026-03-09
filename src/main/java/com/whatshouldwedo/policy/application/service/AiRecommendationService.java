package com.whatshouldwedo.policy.application.service;

import com.whatshouldwedo.policy.application.port.in.output.result.AiRecommendationResult;
import com.whatshouldwedo.policy.application.port.out.AiApiPort;
import com.whatshouldwedo.policy.application.port.out.AiRecommendationRepository;
import com.whatshouldwedo.policy.domain.AiRecommendation;
import com.whatshouldwedo.policy.domain.AiRecommendationId;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {

    private final AiRecommendationRepository aiRecommendationRepository;
    private final AiApiPort aiApiPort;

    @Transactional
    public AiRecommendationResult requestRecommendation(String requestedBy, String regionCode,
                                                          EStatisticsCategory category) {
        String prompt = buildPrompt(regionCode, category);
        AiApiPort.AiApiResponse response = aiApiPort.requestRecommendation(prompt);

        AiRecommendation recommendation = AiRecommendation.create(
                AiRecommendationId.generate(), requestedBy, regionCode, category,
                response.reasoning(), response.recommendedPledge(),
                response.expectedEffect(), response.estimatedBudget(),
                response.sources(), prompt, response.rawResponse()
        );

        AiRecommendation saved = aiRecommendationRepository.save(recommendation);
        return AiRecommendationResult.from(saved);
    }

    @Transactional(readOnly = true)
    public List<AiRecommendationResult> getRecommendations(String requestedBy) {
        return aiRecommendationRepository.findAllByRequestedBy(requestedBy).stream()
                .map(AiRecommendationResult::from).toList();
    }

    private String buildPrompt(String regionCode, EStatisticsCategory category) {
        return String.format(
                "지역코드 %s의 %s 분야 통계 데이터를 분석하여 " +
                "해당 지역에 적합한 선거 공약을 추천해주세요. " +
                "추론 근거, 공약 내용, 기대 효과, 예상 예산을 포함해주세요.",
                regionCode, category.getDescription()
        );
    }
}
