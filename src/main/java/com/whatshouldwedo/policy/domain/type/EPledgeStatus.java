package com.whatshouldwedo.policy.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EPledgeStatus {
    DRAFT("초안"),
    PUBLISHED("공개"),
    ARCHIVED("보관");

    private final String description;
}
