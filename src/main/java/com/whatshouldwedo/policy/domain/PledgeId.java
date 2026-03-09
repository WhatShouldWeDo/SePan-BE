package com.whatshouldwedo.policy.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class PledgeId extends BaseId<UUID> {

    private PledgeId(UUID value) {
        super(value);
    }

    public static PledgeId generate() {
        return new PledgeId(generateUUID());
    }

    public static PledgeId of(UUID value) {
        return new PledgeId(value);
    }

    public static PledgeId of(String value) {
        return new PledgeId(UUID.fromString(value));
    }
}
