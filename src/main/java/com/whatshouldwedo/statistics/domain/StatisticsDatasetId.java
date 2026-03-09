package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class StatisticsDatasetId extends BaseId<UUID> {

    private StatisticsDatasetId(UUID value) {
        super(value);
    }

    public static StatisticsDatasetId generate() {
        return new StatisticsDatasetId(generateUUID());
    }

    public static StatisticsDatasetId of(UUID value) {
        return new StatisticsDatasetId(value);
    }

    public static StatisticsDatasetId of(String value) {
        return new StatisticsDatasetId(UUID.fromString(value));
    }
}
