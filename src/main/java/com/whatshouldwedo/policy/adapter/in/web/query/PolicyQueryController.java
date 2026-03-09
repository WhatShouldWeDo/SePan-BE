package com.whatshouldwedo.policy.adapter.in.web.query;

import com.whatshouldwedo.core.annotation.bean.CurrentUser;
import com.whatshouldwedo.core.dto.ResponseDto;
import com.whatshouldwedo.policy.application.port.in.output.result.AiRecommendationResult;
import com.whatshouldwedo.policy.application.port.in.output.result.PledgeResult;
import com.whatshouldwedo.policy.application.service.AiRecommendationService;
import com.whatshouldwedo.policy.application.service.PledgeService;
import com.whatshouldwedo.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PolicyQueryController {

    private final PledgeService pledgeService;
    private final AiRecommendationService aiRecommendationService;

    @GetMapping("/pledges")
    public ResponseDto<List<PledgeResult>> getPledges(@CurrentUser UserId userId) {
        return ResponseDto.ok(pledgeService.getPledgesByAuthor(userId.getValue().toString()));
    }

    @GetMapping("/pledges/{pledgeId}")
    public ResponseDto<PledgeResult> getPledge(@PathVariable String pledgeId) {
        return ResponseDto.ok(pledgeService.getPledge(pledgeId));
    }

    @GetMapping("/pledges/historical")
    public ResponseDto<List<PledgeResult>> getHistoricalPledges() {
        return ResponseDto.ok(pledgeService.getPublishedPledges());
    }

    @GetMapping("/ai-recommendations")
    public ResponseDto<List<AiRecommendationResult>> getRecommendations(@CurrentUser UserId userId) {
        return ResponseDto.ok(aiRecommendationService.getRecommendations(userId.getValue().toString()));
    }
}
