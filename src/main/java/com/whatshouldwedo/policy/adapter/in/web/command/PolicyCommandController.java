package com.whatshouldwedo.policy.adapter.in.web.command;

import com.whatshouldwedo.core.annotation.bean.CurrentUser;
import com.whatshouldwedo.core.dto.ResponseDto;
import com.whatshouldwedo.policy.adapter.in.web.dto.request.CreateAiRecommendationRequestDto;
import com.whatshouldwedo.policy.adapter.in.web.dto.request.CreatePledgeRequestDto;
import com.whatshouldwedo.policy.adapter.in.web.dto.request.UpdatePledgeRequestDto;
import com.whatshouldwedo.policy.application.port.in.output.result.AiRecommendationResult;
import com.whatshouldwedo.policy.application.port.in.output.result.PledgeResult;
import com.whatshouldwedo.policy.application.service.AiRecommendationService;
import com.whatshouldwedo.policy.application.service.PledgeService;
import com.whatshouldwedo.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PolicyCommandController {

    private final PledgeService pledgeService;
    private final AiRecommendationService aiRecommendationService;

    @PostMapping("/pledges")
    public ResponseDto<PledgeResult> createPledge(
            @CurrentUser UserId userId,
            @RequestBody CreatePledgeRequestDto request) {
        PledgeResult result = pledgeService.createPledge(
                userId.getValue().toString(), request.title(), request.content(),
                request.electionId(), request.districtId(),
                request.relatedCategory(), request.regionCode()
        );
        return ResponseDto.created(result);
    }

    @PutMapping("/pledges/{pledgeId}")
    public ResponseDto<PledgeResult> updatePledge(
            @PathVariable String pledgeId,
            @RequestBody UpdatePledgeRequestDto request) {
        PledgeResult result = pledgeService.updatePledge(pledgeId, request.title(), request.content());
        return ResponseDto.ok(result);
    }

    @PostMapping("/ai-recommendations")
    public ResponseDto<AiRecommendationResult> createRecommendation(
            @CurrentUser UserId userId,
            @RequestBody CreateAiRecommendationRequestDto request) {
        AiRecommendationResult result = aiRecommendationService.requestRecommendation(
                userId.getValue().toString(), request.regionCode(), request.relatedCategory()
        );
        return ResponseDto.created(result);
    }
}
