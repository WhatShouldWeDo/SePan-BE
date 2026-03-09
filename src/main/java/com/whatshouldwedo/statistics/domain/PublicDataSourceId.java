package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class PublicDataSourceId extends BaseId<UUID> {

    private PublicDataSourceId(UUID value) {
        super(value);
    }

    public static PublicDataSourceId generate() {
        return new PublicDataSourceId(generateUUID());
    }

    public static PublicDataSourceId of(UUID value) {
        return new PublicDataSourceId(value);
    }

    public static PublicDataSourceId of(String value) {
        return new PublicDataSourceId(UUID.fromString(value));
    }
}
