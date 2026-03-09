package com.whatshouldwedo.election.application.port.in.output.result;

import com.whatshouldwedo.election.domain.Election;
import com.whatshouldwedo.election.domain.type.EElectionType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ElectionResult {
    private final String id;
    private final String name;
    private final EElectionType electionType;
    private final LocalDate electionDate;
    private final String hjdongVersionId;
    private final String description;

    private ElectionResult(String id, String name, EElectionType electionType,
                            LocalDate electionDate, String hjdongVersionId, String description) {
        this.id = id;
        this.name = name;
        this.electionType = electionType;
        this.electionDate = electionDate;
        this.hjdongVersionId = hjdongVersionId;
        this.description = description;
    }

    public static ElectionResult from(Election election) {
        return new ElectionResult(
                election.getId().getValue().toString(),
                election.getName(),
                election.getElectionType(),
                election.getElectionDate(),
                election.getHjdongVersionId().getValue().toString(),
                election.getDescription()
        );
    }
}
