package com.whatshouldwedo.region.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class SidoId extends BaseId<UUID> {

    private SidoId(UUID value) {
        super(value);
    }

    public static SidoId generate() {
        return new SidoId(generateUUID());
    }

    public static SidoId of(UUID value) {
        return new SidoId(value);
    }

    public static SidoId of(String value) {
        return new SidoId(UUID.fromString(value));
    }
}
