package com.whatshouldwedo.region.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class HjdongVersionId extends BaseId<UUID> {

    private HjdongVersionId(UUID value) {
        super(value);
    }

    public static HjdongVersionId generate() {
        return new HjdongVersionId(generateUUID());
    }

    public static HjdongVersionId of(UUID value) {
        return new HjdongVersionId(value);
    }

    public static HjdongVersionId of(String value) {
        return new HjdongVersionId(UUID.fromString(value));
    }
}
