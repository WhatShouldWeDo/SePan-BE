package com.whatshouldwedo.election.adapter.out.persistence.jpa;

import com.whatshouldwedo.election.domain.Election;
import com.whatshouldwedo.election.domain.ElectionId;
import com.whatshouldwedo.election.domain.type.EElectionType;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "elections")
public class ElectionJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "election_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private EElectionType electionType;

    @Column(name = "election_date", nullable = false)
    private LocalDate electionDate;

    @Column(name = "hjdong_version_id", nullable = false, length = 36)
    private String hjdongVersionId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static ElectionJpaEntity fromDomain(Election election) {
        ElectionJpaEntity entity = new ElectionJpaEntity();
        entity.id = election.getId().getValue().toString();
        entity.name = election.getName();
        entity.electionType = election.getElectionType();
        entity.electionDate = election.getElectionDate();
        entity.hjdongVersionId = election.getHjdongVersionId().getValue().toString();
        entity.description = election.getDescription();
        entity.createdAt = election.getCreatedAt();
        entity.updatedAt = election.getUpdatedAt();
        return entity;
    }

    public Election toDomain() {
        return Election.reconstitute(
                ElectionId.of(id), name, electionType, electionDate,
                HjdongVersionId.of(hjdongVersionId), description, createdAt, updatedAt
        );
    }
}
