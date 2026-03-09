package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class UploadHistoryId extends BaseId<UUID> {

    private UploadHistoryId(UUID value) {
        super(value);
    }

    public static UploadHistoryId generate() {
        return new UploadHistoryId(generateUUID());
    }

    public static UploadHistoryId of(UUID value) {
        return new UploadHistoryId(value);
    }

    public static UploadHistoryId of(String value) {
        return new UploadHistoryId(UUID.fromString(value));
    }
}
