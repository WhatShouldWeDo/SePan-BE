package com.whatshouldwedo.policy.application.port.in.output.result;

import com.whatshouldwedo.policy.domain.Pledge;
import com.whatshouldwedo.policy.domain.type.EPledgeStatus;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PledgeResult {
    private final String id;
    private final String electionId;
    private final String districtId;
    private final String authorId;
    private final String title;
    private final String content;
    private final EStatisticsCategory relatedCategory;
    private final EPledgeStatus status;
    private final String regionCode;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private PledgeResult(String id, String electionId, String districtId, String authorId,
                          String title, String content, EStatisticsCategory relatedCategory,
                          EPledgeStatus status, String regionCode,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.electionId = electionId;
        this.districtId = districtId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.relatedCategory = relatedCategory;
        this.status = status;
        this.regionCode = regionCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PledgeResult from(Pledge pledge) {
        return new PledgeResult(
                pledge.getId().getValue().toString(),
                pledge.getElectionId(), pledge.getDistrictId(), pledge.getAuthorId(),
                pledge.getTitle(), pledge.getContent(), pledge.getRelatedCategory(),
                pledge.getStatus(), pledge.getRegionCode(),
                pledge.getCreatedAt(), pledge.getUpdatedAt()
        );
    }
}
