package com.whatshouldwedo.election.adapter.in.web.dto.request;

import com.whatshouldwedo.election.domain.type.EElectionType;

import java.time.LocalDate;

public record CreateElectionRequestDto(
        String name,
        EElectionType electionType,
        LocalDate electionDate,
        String hjdongVersionId,
        String description
) {
}
