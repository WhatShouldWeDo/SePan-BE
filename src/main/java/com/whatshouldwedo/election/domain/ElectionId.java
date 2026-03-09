package com.whatshouldwedo.election.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class ElectionId extends BaseId<UUID> {

    private ElectionId(UUID value) {
        super(value);
    }

    public static ElectionId generate() {
        return new ElectionId(generateUUID());
    }

    public static ElectionId of(UUID value) {
        return new ElectionId(value);
    }

    public static ElectionId of(String value) {
        return new ElectionId(UUID.fromString(value));
    }
}
