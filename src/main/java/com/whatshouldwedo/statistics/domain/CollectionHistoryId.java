package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class CollectionHistoryId extends BaseId<UUID> {

    private CollectionHistoryId(UUID value) {
        super(value);
    }

    public static CollectionHistoryId generate() {
        return new CollectionHistoryId(generateUUID());
    }

    public static CollectionHistoryId of(UUID value) {
        return new CollectionHistoryId(value);
    }

    public static CollectionHistoryId of(String value) {
        return new CollectionHistoryId(UUID.fromString(value));
    }
}
