package com.whatshouldwedo.policy.domain;

import com.whatshouldwedo.policy.domain.type.EPledgeStatus;
import com.whatshouldwedo.statistics.domain.type.EStatisticsCategory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Pledge {
    private final PledgeId id;
    private String electionId;
    private String districtId;
    private final String authorId;
    private final String title;
    private String content;
    private EStatisticsCategory relatedCategory;
    private EPledgeStatus status;
    private String regionCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Pledge(PledgeId id, String authorId, String title, String content) {
        this.id = Objects.requireNonNull(id);
        this.authorId = Objects.requireNonNull(authorId);
        this.title = Objects.requireNonNull(title);
        this.content = Objects.requireNonNull(content);
        this.status = EPledgeStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Pledge create(PledgeId id, String authorId, String title, String content,
                                  String electionId, String districtId,
                                  EStatisticsCategory relatedCategory, String regionCode) {
        Pledge pledge = new Pledge(id, authorId, title, content);
        pledge.electionId = electionId;
        pledge.districtId = districtId;
        pledge.relatedCategory = relatedCategory;
        pledge.regionCode = regionCode;
        return pledge;
    }

    public static Pledge reconstitute(PledgeId id, String electionId, String districtId,
                                        String authorId, String title, String content,
                                        EStatisticsCategory relatedCategory, EPledgeStatus status,
                                        String regionCode,
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        Pledge pledge = new Pledge(id, authorId, title, content);
        pledge.electionId = electionId;
        pledge.districtId = districtId;
        pledge.relatedCategory = relatedCategory;
        pledge.status = status;
        pledge.regionCode = regionCode;
        pledge.createdAt = createdAt;
        pledge.updatedAt = updatedAt;
        return pledge;
    }

    public void update(String title, String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void publish() {
        this.status = EPledgeStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }

    public void archive() {
        this.status = EPledgeStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }
}
