package com.whatshouldwedo.region.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class HjdongMappingId extends BaseId<UUID> {

    private HjdongMappingId(UUID value) {
        super(value);
    }

    public static HjdongMappingId generate() {
        return new HjdongMappingId(generateUUID());
    }

    public static HjdongMappingId of(UUID value) {
        return new HjdongMappingId(value);
    }

    public static HjdongMappingId of(String value) {
        return new HjdongMappingId(UUID.fromString(value));
    }
}
