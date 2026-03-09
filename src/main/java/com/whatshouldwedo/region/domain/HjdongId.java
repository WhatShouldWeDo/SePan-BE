package com.whatshouldwedo.region.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class HjdongId extends BaseId<UUID> {

    private HjdongId(UUID value) {
        super(value);
    }

    public static HjdongId generate() {
        return new HjdongId(generateUUID());
    }

    public static HjdongId of(UUID value) {
        return new HjdongId(value);
    }

    public static HjdongId of(String value) {
        return new HjdongId(UUID.fromString(value));
    }
}
