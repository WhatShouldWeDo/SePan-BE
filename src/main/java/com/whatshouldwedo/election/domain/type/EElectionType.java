package com.whatshouldwedo.election.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EElectionType {
    PRESIDENTIAL("대통령 선거"),
    LOCAL("지방 선거"),
    GENERAL("총선");

    private final String description;
}
