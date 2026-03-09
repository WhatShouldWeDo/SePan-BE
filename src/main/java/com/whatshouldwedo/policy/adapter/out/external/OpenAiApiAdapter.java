package com.whatshouldwedo.policy.adapter.out.external;

import com.whatshouldwedo.policy.application.port.out.AiApiPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAiApiAdapter implements AiApiPort {

    @Value("${openai.api-key:}")
    private String apiKey;

    @Override
    public AiApiResponse requestRecommendation(String prompt) {
        // TODO: OpenAI API 연동 구현
        // 현재는 placeholder 반환
        return new AiApiResponse(
                "AI 분석 결과에 기반한 추론",
                "추천 공약 내용",
                "기대 효과",
                "예상 예산",
                "데이터 소스",
                "raw response placeholder"
        );
    }
}
