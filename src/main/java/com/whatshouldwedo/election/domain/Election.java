package com.whatshouldwedo.election.domain;

import com.whatshouldwedo.election.domain.type.EElectionType;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Election {
    private final ElectionId id;
    private final String name;
    private final EElectionType electionType;
    private final LocalDate electionDate;
    private final HjdongVersionId hjdongVersionId;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Election(ElectionId id, String name, EElectionType electionType,
                     LocalDate electionDate, HjdongVersionId hjdongVersionId) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.electionType = Objects.requireNonNull(electionType);
        this.electionDate = Objects.requireNonNull(electionDate);
        this.hjdongVersionId = Objects.requireNonNull(hjdongVersionId);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Election create(ElectionId id, String name, EElectionType electionType,
                                    LocalDate electionDate, HjdongVersionId hjdongVersionId,
                                    String description) {
        Election election = new Election(id, name, electionType, electionDate, hjdongVersionId);
        election.description = description;
        return election;
    }

    public static Election reconstitute(ElectionId id, String name, EElectionType electionType,
                                          LocalDate electionDate, HjdongVersionId hjdongVersionId,
                                          String description,
                                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        Election election = new Election(id, name, electionType, electionDate, hjdongVersionId);
        election.description = description;
        election.createdAt = createdAt;
        election.updatedAt = updatedAt;
        return election;
    }
}
