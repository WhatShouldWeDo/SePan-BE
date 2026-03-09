package com.whatshouldwedo.policy.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.policy.application.port.in.output.result.PledgeResult;
import com.whatshouldwedo.policy.application.port.out.PledgeRepository;
import com.whatshouldwedo.policy.domain.Pledge;
import com.whatshouldwedo.policy.domain.PledgeId;
import com.whatshouldwedo.policy.domain.type.EPledgeStatus;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PledgeService {

    private final PledgeRepository pledgeRepository;

    @Transactional
    public PledgeResult createPledge(String authorId, String title, String content,
                                      String electionId, String districtId,
                                      EStatisticsCategory relatedCategory, String regionCode) {
        Pledge pledge = Pledge.create(PledgeId.generate(), authorId, title, content,
                electionId, districtId, relatedCategory, regionCode);
        Pledge saved = pledgeRepository.save(pledge);
        return PledgeResult.from(saved);
    }

    @Transactional(readOnly = true)
    public List<PledgeResult> getPledgesByAuthor(String authorId) {
        return pledgeRepository.findAllByAuthorId(authorId).stream()
                .map(PledgeResult::from).toList();
    }

    @Transactional(readOnly = true)
    public PledgeResult getPledge(String pledgeId) {
        Pledge pledge = pledgeRepository.findById(PledgeId.of(pledgeId))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PLEDGE));
        return PledgeResult.from(pledge);
    }

    @Transactional
    public PledgeResult updatePledge(String pledgeId, String title, String content) {
        Pledge pledge = pledgeRepository.findById(PledgeId.of(pledgeId))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PLEDGE));
        pledge.update(title, content);
        Pledge saved = pledgeRepository.save(pledge);
        return PledgeResult.from(saved);
    }

    @Transactional(readOnly = true)
    public List<PledgeResult> getPublishedPledges() {
        return pledgeRepository.findAllByStatus(EPledgeStatus.PUBLISHED).stream()
                .map(PledgeResult::from).toList();
    }
}
