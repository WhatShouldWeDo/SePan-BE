package com.whatshouldwedo.election.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class ElectoralDistrictId extends BaseId<UUID> {

    private ElectoralDistrictId(UUID value) {
        super(value);
    }

    public static ElectoralDistrictId generate() {
        return new ElectoralDistrictId(generateUUID());
    }

    public static ElectoralDistrictId of(UUID value) {
        return new ElectoralDistrictId(value);
    }

    public static ElectoralDistrictId of(String value) {
        return new ElectoralDistrictId(UUID.fromString(value));
    }
}
