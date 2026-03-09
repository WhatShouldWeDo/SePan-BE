package com.whatshouldwedo.statistics.domain;

import com.whatshouldwedo.core.BaseId;

import java.util.UUID;

public class StatisticsCategoryId extends BaseId<UUID> {

    private StatisticsCategoryId(UUID value) {
        super(value);
    }

    public static StatisticsCategoryId generate() {
        return new StatisticsCategoryId(generateUUID());
    }

    public static StatisticsCategoryId of(UUID value) {
        return new StatisticsCategoryId(value);
    }

    public static StatisticsCategoryId of(String value) {
        return new StatisticsCategoryId(UUID.fromString(value));
    }
}
