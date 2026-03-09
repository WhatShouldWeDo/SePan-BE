package com.whatshouldwedo.policy.adapter.out.persistence.jpa;

import com.whatshouldwedo.policy.domain.Pledge;
import com.whatshouldwedo.policy.domain.PledgeId;
import com.whatshouldwedo.policy.domain.type.EPledgeStatus;
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
@Table(name = "pledges")
public class PledgeJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "election_id", length = 36)
    private String electionId;

    @Column(name = "district_id", length = 36)
    private String districtId;

    @Column(name = "author_id", nullable = false, length = 36)
    private String authorId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "related_category", length = 30)
    @Enumerated(EnumType.STRING)
    private EStatisticsCategory relatedCategory;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EPledgeStatus status;

    @Column(name = "region_code", length = 15)
    private String regionCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static PledgeJpaEntity fromDomain(Pledge pledge) {
        PledgeJpaEntity entity = new PledgeJpaEntity();
        entity.id = pledge.getId().getValue().toString();
        entity.electionId = pledge.getElectionId();
        entity.districtId = pledge.getDistrictId();
        entity.authorId = pledge.getAuthorId();
        entity.title = pledge.getTitle();
        entity.content = pledge.getContent();
        entity.relatedCategory = pledge.getRelatedCategory();
        entity.status = pledge.getStatus();
        entity.regionCode = pledge.getRegionCode();
        entity.createdAt = pledge.getCreatedAt();
        entity.updatedAt = pledge.getUpdatedAt();
        return entity;
    }

    public Pledge toDomain() {
        return Pledge.reconstitute(
                PledgeId.of(id), electionId, districtId, authorId,
                title, content, relatedCategory, status, regionCode,
                createdAt, updatedAt
        );
    }
}
