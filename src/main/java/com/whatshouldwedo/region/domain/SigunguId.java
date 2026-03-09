package com.whatshouldwedo.region.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class SigunguId extends BaseId<UUID> {

    private SigunguId(UUID value) {
        super(value);
    }

    public static SigunguId generate() {
        return new SigunguId(generateUUID());
    }

    public static SigunguId of(UUID value) {
        return new SigunguId(value);
    }

    public static SigunguId of(String value) {
        return new SigunguId(UUID.fromString(value));
    }
}
